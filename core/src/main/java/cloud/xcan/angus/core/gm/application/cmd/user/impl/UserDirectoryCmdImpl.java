package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.spring.SpringContextHolder.getCachedUidGenerator;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.longSafe;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.manager.GroupManager;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.api.obf.Str0;
import cloud.xcan.angus.api.pojo.Pair;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserDirectoryCmd;
import cloud.xcan.angus.core.gm.application.converter.GroupConverter;
import cloud.xcan.angus.core.gm.application.converter.UserConverter;
import cloud.xcan.angus.core.gm.application.query.user.UserDirectoryQuery;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectoryRepo;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryServer;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySyncResult;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.crypto.AESUtils;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Slf4j
public class UserDirectoryCmdImpl extends CommCmd<UserDirectory, Long> implements
    UserDirectoryCmd {

  @Resource
  private UserDirectoryRepo userDirectoryRepo;

  @Resource
  private UserDirectoryQuery userDirectoryQuery;

  @Resource
  private AuthUserRepo authUserRepo;

  @Resource
  private UserCmd userCmd;

  @Resource
  private GroupCmd groupCmd;

  @Resource
  private GroupUserCmd userGroupCmd;

  @Resource
  private TenantManager tenantManager;

  @Resource
  private UserManager userManager;

  @Resource
  private GroupManager groupManager;

  // @Transactional(rollbackFor = Exception.class)
  // Synchronization can be unsuccessful when saving successfully
  @Override
  public IdKey<Long, Object> add(UserDirectory directory, boolean onlyTest) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Ignore check when test directory
        if (!onlyTest) {
          // Check name exists
          userDirectoryQuery.checkNameExisted(directory);

          // Check server quota
          userDirectoryQuery.checkQuota(1);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Calculation sequence, increasing by default
        if (!onlyTest) {
          if (Objects.isNull(directory.getSequence()) || directory.getSequence() <= 0) {
            calcDefaultSequence(directory);
          }
        }

        // LDAP server password encryption storage
        if (!onlyTest) { // Fix:: Test server passd encrypt is repeated
          directory.getServerData()
              .setPassword(encryptServerPassword(directory.getServerData().getPassword()));
        }

        // Save directory and use inner @Transactional
        IdKey<Long, Object> idKey = insert(directory, "name");

        // Synchronize users and groups from the directory
        // No rollback when an exception occurs
        if (directory.getEnabled()) {
          sync(directory.getId(), onlyTest);
        }
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(UserDirectory directory) {
    new BizTemplate<Void>() {
      UserDirectory directoryDb;

      @Override
      protected void checkParams() {
        // Check name exists
        userDirectoryQuery.checkNameExisted(directory);
        // Check directory exists
        if (nonNull(directory.getId())) {
          directoryDb = userDirectoryQuery.checkAndFind(directory.getId());
        }
      }

      @Override
      protected Void process() {
        if (Objects.isNull(directoryDb)) {
          add(directory, false);
          return null;
        }

        // LDAP server password encryption storage
        // Password has been modified
        if (!directory.getServerData().getPassword()
            .equals(directoryDb.getServerData().getPassword())) {
          directory.getServerData()
              .setPassword(encryptServerPassword(directory.getServerData().getPassword()));
        }

        // Save directory
        batchUpdate0(Collections.singletonList(CoreUtils.copyPropertiesIgnoreAuditing(directory,
            directoryDb, "enabled")));
        // Synchronize users and groups from the directory
        // No rollback when an exception occurs
        if (directoryDb.getEnabled()) {
          sync(directory.getId(), false);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void reorder(Map<Long, Integer> directorySequences) {
    new BizTemplate<Void>() {
      List<UserDirectory> userDirectoriesDb;

      @Override
      protected void checkParams() {
        // Check exists
        userDirectoriesDb = userDirectoryQuery.checkAndFind(directorySequences.keySet());
      }

      @Override
      protected Void process() {
        for (UserDirectory directory : userDirectoriesDb) {
          directory.setSequence(directorySequences.get(directory.getId()));
        }
        batchUpdate0(userDirectoriesDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<UserDirectory> directories) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        batchUpdateOrNotFound(directories);
        return null;
      }
    }.execute();
  }

  /**
   * ***Important:: The number of synchronization users cannot exceed the quota limit.***
   * <p>
   * Duplicate judgment:
   * <p>
   * - Group Sync: When synchronizing a group, the code must not be repeated. When the code is not
   * repeated, judge whether the name needs to be removed duplicates according to
   * ignoreSameNameGroup flag.
   * <p>
   * - User Sync: Force ignore the same identity(unique name, email, mobile) user.
   * <p>
   */
  @Override
  public DirectorySyncResult sync(Long id, boolean onlyTest) {
    return new BizTemplate<DirectorySyncResult>() {
      UserDirectory directoryDb = null;
      Tenant tenantDb = null;

      @Override
      protected void checkParams() {
        directoryDb = userDirectoryQuery.checkAndFind(id);
        ProtocolAssert.assertTrue(directoryDb.getEnabled(), "Directory is disabled");
        tenantDb = tenantManager.checkAndFindOwnerTenant();
      }

      @Override
      protected DirectorySyncResult process() {
        if (!isUserAction()) {
          PrincipalContext.create();
        }
        PrincipalContext.get().setOptTenantId(tenantDb.getId());
        PrincipalContext.get().setTenantName(tenantDb.getName());

        DirectorySyncResult result = new DirectorySyncResult();

        // 1. Verify Host and auth information
        tryConnectLdapServer(result, directoryDb, onlyTest);
        if (!result.getConnectSuccess()) {
          result.setSuccess(false);
          return result;
        }

        try {
          LdapTemplate ldapTemplate = getLdapTemplate(directoryDb, onlyTest);

          // 2. Sync groups
          // 2.1. Find the latest groups in the directory
          List<Group> groupsLdap = searchGroup(directoryDb, ldapTemplate);
          result.setTotalGroupNum(groupsLdap.size());
          // Find the latest groups in the DB
          List<Group> groupsDb = groupManager.findByTenantId(tenantDb.getId());
          Map<String, List<Group>> nameGroupDbMap = groupsDb.stream()
              .collect(Collectors.groupingBy(Group::getName));
          Map<String, Group> codeGroupDbMap = groupsDb.stream()
              .collect(Collectors.toMap(Group::getCode, x -> x));

          // 2.2. Calculate group sync results
          // 1).New groups in the directory
          List<Group> addGroups = findNewGroupsInDirectory(groupsLdap, nameGroupDbMap,
              codeGroupDbMap, directoryDb);
          if (isNotEmpty(addGroups)) {
            if (!onlyTest) {
              groupCmd.add(addGroups);
            }
            result.setAddGroupNum(addGroups.size());
          }
          // 2).Update groups in the directory
          List<Group> updateGroups = findUpdateGroupsInDirectory(groupsLdap, nameGroupDbMap,
              codeGroupDbMap, directoryDb);
          if (isNotEmpty(updateGroups)) {
            if (!onlyTest) {
              groupCmd.update(updateGroups);
            }
            result.setUpdateGroupNum(updateGroups.size());
          }
          // 3).Delete groups in the directory
          List<Group> deleteGroups = findDeleteGroupsInDirectory(groupsLdap,
              codeGroupDbMap, directoryDb);
          Set<Long> deleteGroupIds = deleteGroups.stream().map(Group::getId)
              .collect(Collectors.toSet());
          if (isNotEmpty(deleteGroups)) {
            if (!onlyTest) {
              // Keep deleted data
              // groupCmd.delete(deleteGroupIds);
              groupCmd.emptyDirectoryGroups(deleteGroupIds);
            }
            result.setDeleteGroupNum(deleteGroups.size());
          }
          // 4).Ignore groups number in the directory
          result.setIgnoreGroupNum(groupsLdap.size() - addGroups.size() - updateGroups.size());
          result.setGroupSuccess(true);

          // 3. Sync users
          // 3.1. Find the latest users in the directory
          List<User> usersLdap = searchUser(directoryDb, ldapTemplate);
          result.setTotalUserNum(usersLdap.size());
          // Find the latest users in the DB
          List<User> usersDb = userManager.findByTenantId(tenantDb.getId());
          if (isNotEmpty(usersDb)) {
            Map<Long, AuthUser> authUserMap = authUserRepo.findAllByTenantId(
                    tenantDb.getId().toString()).stream()
                .collect(Collectors.toMap(x -> Long.parseLong(x.getId()), x -> x));
            if (isNotEmpty(authUserMap)) {
              for (User user : usersDb) {
                // Insert AuthUser in AAS when no user triggers modification
                if (nonNull(authUserMap.get(user.getId()))) {
                  user.setPassword(authUserMap.get(user.getId()).getPassword());
                  user.setTenantRealNameStatus(TenantRealNameStatus.valueOf(
                      authUserMap.get(user.getId()).getTenantRealNameStatus()));
                }
              }
            }
          }
          Map<String, User> usernameUserDbMap = usersDb.stream()
              .collect(Collectors.toMap(User::getUsername, x -> x));
          Map<String, User> emailUserDbMap = usersDb.stream().filter(x -> isNotEmpty(x.getEmail()))
              .collect(Collectors.toMap(User::getEmail, x -> x));
          Map<String, User> mobileUserDbMap = usersDb.stream()
              .filter(x -> isNotEmpty(x.getMobile()))
              .collect(Collectors.toMap(User::getMobile, x -> x));

          // 3.2. Calculate user sync results
          // 1).New users in the directory
          List<User> addUsers = findNewUsersInDirectory(usersLdap, usernameUserDbMap,
              emailUserDbMap, mobileUserDbMap);
          if (isNotEmpty(addUsers)) {
            for (User user : addUsers) {
              if (!onlyTest) {
                userCmd.add(user, null, null, null, UserSource.LDAP_SYNCHRONIZE);
              }
              result.setAddUserNum(result.getAddUserNum() + 1);
            }
          }
          // 2).Update users in the directory
          List<User> updateUsers = findUpdateUsersInDirectory(usersLdap, usernameUserDbMap,
              directoryDb, tenantDb);
          if (isNotEmpty(updateUsers)) {
            for (User updateUser : updateUsers) {
              if (!onlyTest) {
                userCmd.update(updateUser, null, null, null);
              }
              result.setUpdateUserNum(result.getUpdateUserNum() + 1);
            }
          }
          // 3).Delete users in the directory
          List<User> deleteUsers = findDeleteUsersInDirectory(usersLdap, usernameUserDbMap,
              directoryDb);
          Set<Long> deleteUserIds = deleteUsers.stream().map(User::getId)
              .collect(Collectors.toSet());
          if (isNotEmpty(deleteUsers)) {
            if (!onlyTest) {
              // Keep deleted data
              //deleteGroupUserRelationshipNum = userCmd.deleteDirectoryUsers(deleteUserIds);
              userCmd.emptyDirectoryUsers(deleteUserIds);
            }
            result.setDeleteUserNum(deleteUsers.size());
          }
          // 4).Ignore users number in the directory
          result.setIgnoreUserNum(usersLdap.size() - addUsers.size() - updateUsers.size());
          result.setUserSuccess(true);

          // 4. Sync membership when the user and group are sync successfully
          if (result.getUserSuccess() && result.getGroupSuccess()) {
            // 4.1. Find the latest membership in the directory
            List<Group> directoryGroupsDb = groupManager.find(tenantDb.getId(),
                directoryDb.getId());
            if (isNotEmpty(directoryGroupsDb)) {
              List<GroupUser> membershipInLdap = assembleGroupUserAssociation(ldapTemplate,
                  tenantDb, directoryDb, directoryGroupsDb, groupsLdap);
              // Fix: There are non current directory member relationships present
              // Means that reinserting duplicate member relationships is ignored
              // List<GroupUser> membershipInDb = groupManager.findGroupUser(tenantDb.getId(),directoryDb.getId());
              List<GroupUser> membershipInDb = groupManager.findGroupUserByTenantId(
                  tenantDb.getId());
              // New membership
              List<GroupUser> newAddMembership = new ArrayList<>(membershipInLdap);
              CoreUtils.removeAll(newAddMembership, membershipInDb);
              List<GroupUser> newAddMembershipDistinct = CoreUtils.distinct(newAddMembership);
              if (!onlyTest && !newAddMembershipDistinct.isEmpty()) {
                userGroupCmd.add0(newAddMembershipDistinct);
              }
              result.setAddMembershipNum(newAddMembershipDistinct.size());

              // Deleted membership
              List<GroupUser> deletedMembership = new ArrayList<>(membershipInDb);
              CoreUtils.removeAll(deletedMembership, membershipInLdap);
              if (!onlyTest && !deletedMembership.isEmpty()) {
                userGroupCmd.delete0(directoryDb.getId(), deletedMembership);
              }
              result.setDeleteMembershipNum(deletedMembership.size());
            }

            // 4.2. Calculate membership sync results
            result.setMembershipSuccess(true);
          }

          result.setSuccess(true);
          log.info("Synchronize user directory result: {}", result.toString());
          log.info("Synchronize user directory successfully");
          return result;
        } catch (Exception e) {
          result.setSuccess(false);
          result.setErrorMessage(e.getMessage());
          log.error("Synchronize user directory result: {}", result.toString());
          log.error("Synchronize user directory exception:", e);
          return result;
        } finally {
          if (!isUserAction()) {
            PrincipalContext.remove();
          }
        }
      }
    }.execute();
  }

  // @Transactional(rollbackFor = Exception.class)
  @Override
  public Map<String, DirectorySyncResult> sync() {
    return new BizTemplate<Map<String, DirectorySyncResult>>() {
      @Override
      protected Map<String, DirectorySyncResult> process() {
        Map<String, DirectorySyncResult> results = new HashMap<>();
        List<UserDirectory> directories = userDirectoryRepo.findAll();
        if (isNotEmpty(directories)) {
          for (UserDirectory directory : directories) {
            if (directory.getEnabled()) {
              results.put(directory.getName(), sync(directory.getId(), false));
            }
          }
        }
        return results;
      }
    }.execute();
  }

  // @Transactional(rollbackFor = Exception.class)
  @Override
  public DirectorySyncResult test(UserDirectory directory) {
    return new BizTemplate<DirectorySyncResult>() {
      @Override
      protected DirectorySyncResult process() {
        IdKey<Long, Object> idKey = add(directory, true);
        DirectorySyncResult result = sync(idKey.getId(), true);
        delete(idKey.getId(), false);
        return result;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id, boolean deleteSync) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        UserDirectory directory = userDirectoryQuery.find(id);
        if (Objects.isNull(directory)) {
          return null;
        }

        // Delete directory
        userDirectoryRepo.deleteById(id);

        // Delete users and groups
        // Disable LDAP login for deleted directory users
        // @See AAS service LdapPasswordConnection
        userCmd.deleteByDirectory(id, deleteSync);
        groupCmd.deleteByDirectory(id, deleteSync);
        return null;
      }
    }.execute();
  }

  private List<Group> searchGroup(UserDirectory ldap, LdapTemplate template) {
    AndFilter andFilter = new AndFilter();
    if (isNotBlank(ldap.getGroupSchemaData().getObjectFilter())) {
      andFilter.and(new HardcodedFilter(ldap.getGroupSchemaData().getObjectFilter()));
    }
    List<Group> groups = new ArrayList<>();
    template.search(
        query().base(ldap.getSchemaData().getAdditionalGroupDn()).filter(andFilter),
        (AttributesMapper<Group>) attrs -> {
          if (nonNull(attrs)) {
            Group group = GroupConverter.ldapToGroup(attrs, ldap);
            groups.add(group);
          }
          return null;
        });
    return groups;
  }

  private List<User> searchUser(UserDirectory ldap, LdapTemplate template) {
    AndFilter andFilter = new AndFilter();
    if (isNotBlank(ldap.getUserSchemaData().getObjectFilter())) {
      andFilter.append(new HardcodedFilter(ldap.getUserSchemaData().getObjectFilter()));
    }
    // Search one user by username, search filter: (uid={0})
    // andFilter.and(new EqualsFilter(ldap.getUserSchemaData().getUsernameAttribute(), username));
    List<User> users = new ArrayList<>();
    template.search(
        query().base(ldap.getSchemaData().getAdditionalUserDn()).filter(andFilter),
        (AttributesMapper<User>) attrs -> {
          if (nonNull(attrs)) {
            User user = UserConverter.ldapToUser(attrs, ldap);
            users.add(user);
          }
          return null;
        });
    return users;
  }

  /**
   * Search membership groups according to user
   */
  private List<GroupUser> searchMemberGroup(UserDirectory directory, LdapTemplate template,
      Group group) {
    if (Objects.isNull(group.getDirectoryGidNumber())) {
      return Collections.emptyList();
    }
    AndFilter andFilter = new AndFilter();
    if (isNotBlank(directory.getGroupSchemaData().getObjectFilter())) {
      andFilter.and(new HardcodedFilter(directory.getUserSchemaData().getObjectFilter()));
    }
    andFilter.append(new EqualsFilter(directory.getMembershipSchemaData()
        .getMemberGroupAttribute(), group.getDirectoryGidNumber()));

    List<GroupUser> memberships = new ArrayList<>();
    template.search(query().base(
            directory.getSchemaData().getAdditionalUserDn()).filter(andFilter),
        (AttributesMapper<GroupUser>) attrs -> {
          if (nonNull(attrs)) {
            GroupUser membership = UserConverter.ldapToMemberUser(attrs, directory, group);
            if (nonNull(membership)) {
              memberships.add(membership);
            }
          }
          return null;
        });
    return memberships;
  }

  private List<GroupUser> assembleGroupUserAssociation(LdapTemplate ldapTemplate, Tenant tenantDb,
      UserDirectory directoryDb, List<Group> directoryGroupsDb, List<Group> groupsLdap) {
    List<GroupUser> groupUsers = new ArrayList<>();
    for (Group group : directoryGroupsDb) {
      // Search membership groups according to user
      List<GroupUser> memberships = searchMemberGroup(directoryDb, ldapTemplate, group);
      if (isNotEmpty(memberships)) {
        groupUsers.addAll(memberships);
      }
    }

    // Search membership users according to group, OPENLDAP group attribute and user lists: "
    //    + " memberUid"
    //    + "  dev.03"
    //    + "  dev.04-sha"
    if (isNotEmpty(groupsLdap) && isNotEmpty(directoryDb.getMembershipSchemaData()
        .getGroupMemberAttribute())) {
      Map<String, Group> directoryGroupsDbMap = directoryGroupsDb.stream()
          .collect(Collectors.toMap(Group::getCode, x -> x));
      for (Group group : groupsLdap) {
        if (isNotEmpty(group.getMembers())) {
          for (String username : group.getMembers()) {
            groupUsers.add(new GroupUser().setId(getCachedUidGenerator().getUID())
                .setDirectoryId(directoryDb.getId())
                .setGroupId(directoryGroupsDbMap.get(group.getCode()).getId())
                .setUsername(username)
                .setGidNumber(group.getDirectoryGidNumber()));
          }
        }
      }
    }

    if (isNotEmpty(groupUsers)) {
      // Latest user data in DB
      Map<String, User> usernameUserMap = userManager.findByTenantIdAndDirectoryId(
              tenantDb.getId(), directoryDb.getId()).stream()
          .collect(Collectors.toMap(User::getUsername, x -> x));
      if (isNotEmpty(usernameUserMap)) {
        for (GroupUser groupUser : groupUsers) {
          User user = usernameUserMap.get(groupUser.getUsername());
          // Fix:: The group relationship in OpenLDAP is not automatically deleted after the user is deleted
          // The relationship exists but the user or group has been deleted
          if (nonNull(user)) {
            groupUser.setTenantId(tenantDb.getId());
            groupUser.setCreatedBy(longSafe(getUserId(), -1L/*By Job*/))
                .setCreatedDate(LocalDateTime.now());
            groupUser.setUserId(user.getId());
          }
        }
      }
    }
    // @DoInFuture("clear relationship")
    return groupUsers.stream().filter(x -> nonNull(x.getUserId())
        && nonNull(x.getGroupId())).collect(Collectors.toList());
  }

  private List<Group> findNewGroupsInDirectory(List<Group> groupsLdap,
      Map<String, List<Group>> nameGroupDbMap, Map<String, Group> codeGroupDbMap,
      UserDirectory directoryDb) {
    if (isEmpty(codeGroupDbMap) /* Empty in DB */ || isEmpty(groupsLdap)) {
      return groupsLdap;
    }

    List<Group> newGroups = new ArrayList<>();
    for (Group groupLdap : groupsLdap) {
      if (!codeGroupDbMap.containsKey(groupLdap.getCode())) {
        if (!directoryDb.getGroupSchemaData().getIgnoreSameNameGroup()
            || !nameGroupDbMap.containsKey(groupLdap.getName())) {
          groupLdap.setTenantId(getOptTenantId())
              .setCreatedBy(longSafe(getUserId(), -1L/*By Job*/))
              .setCreatedDate(LocalDateTime.now())
              .setLastModifiedBy(longSafe(getUserId(), -1L /*By Job*/))
              .setLastModifiedDate(LocalDateTime.now());
          newGroups.add(groupLdap);
        }
      }
    }
    return newGroups;
  }

  private List<Group> findUpdateGroupsInDirectory(List<Group> groupsLdap,
      Map<String, List<Group>> nameGroupDbMap, Map<String, Group> codeGroupDbMap,
      UserDirectory directoryDb) {
    if (isEmpty(codeGroupDbMap) /* Empty in DB */ || isEmpty(groupsLdap)) {
      return Collections.emptyList();
    }
    List<Group> updateGroupsDb = new ArrayList<>();
    boolean syncDescription = nonNull(directoryDb.getGroupSchemaData().getDescriptionAttribute());
    for (Group groupLdap : groupsLdap) {
      Group groupDb = codeGroupDbMap.get(groupLdap.getCode());
      // Updating other directory data is not allowed, must be same directory and entry
      if (nonNull(groupDb) && directoryDb.getId().equals(groupDb.getDirectoryId())) {
        // Only name or name and description may be modified
        if (!groupLdap.getName().equals(groupDb.getName())
            || (syncDescription && !lengthSafe(groupLdap.getRemark(), MAX_DESC_LENGTH)
            .equals(groupDb.getRemark()))) {
          // Ignore modification when name exists
          if (!directoryDb.getGroupSchemaData().getIgnoreSameNameGroup()
              || !nameGroupDbMap.containsKey(groupLdap.getName())) {
            groupDb.setName(groupLdap.getName());
          }
          if (syncDescription) {
            groupDb.setRemark(groupLdap.getRemark());
          }
          groupDb.setLastModifiedBy(longSafe(getUserId(), -1L /*By Job*/))
              .setLastModifiedDate(LocalDateTime.now());
          updateGroupsDb.add(groupDb);
        }
      }
    }
    return updateGroupsDb;
  }

  private List<Group> findDeleteGroupsInDirectory(List<Group> groupsLdap,
      Map<String, Group> codeGroupDbMap, UserDirectory directoryDb) {
    // Filter existed group in DB by directoryId
    // Deleting other directory data is not allowed
    List<Group> existGroupsInDb = codeGroupDbMap.values().stream()
        .filter(x -> directoryDb.getId().equals(x.getDirectoryId())).collect(Collectors.toList());
    if (isEmpty(groupsLdap)) {
      return existGroupsInDb;
    }

    List<Group> deleteGroups = new ArrayList<>();
    Map<String, Group> codeGroupLdapMap = groupsLdap.stream()
        .collect(Collectors.toMap(Group::getCode, x -> x));
    for (Group existGroup : existGroupsInDb) {
      if (!codeGroupLdapMap.containsKey(existGroup.getCode())) {
        deleteGroups.add(existGroup);
      }
    }
    return deleteGroups;
  }

  private List<User> findNewUsersInDirectory(List<User> usersLdap,
      Map<String, User> usernameUserDbMap, Map<String, User> emailUserDbMap,
      Map<String, User> mobileUserDbMap) {
    if (isEmpty(usernameUserDbMap) /* Empty in DB */ || isEmpty(usersLdap)) {
      return usersLdap;
    }
    List<User> newUsers = new ArrayList<>();
    for (User userLdap : usersLdap) {
      if (!usernameUserDbMap.containsKey(userLdap.getUsername())
          && !emailUserDbMap.containsKey(userLdap.getEmail())
          && !mobileUserDbMap.containsKey(userLdap.getMobile())) {
        userLdap.setTenantName(getTenantName());
        userLdap.setTenantId(getOptTenantId())
            .setCreatedBy(longSafe(getUserId(), -1L/*By Job*/))
            .setCreatedDate(LocalDateTime.now())
            .setLastModifiedBy(longSafe(getUserId(), -1L /*By Job*/))
            .setLastModifiedDate(LocalDateTime.now());
        newUsers.add(userLdap);
      }
    }
    return newUsers;
  }

  private List<User> findUpdateUsersInDirectory(List<User> usersLdap,
      Map<String, User> usernameUserDbMap, UserDirectory directoryDb, Tenant tenantDb) {
    if (isEmpty(usernameUserDbMap) /* Empty in DB */ || isEmpty(usersLdap)) {
      return Collections.emptyList();
    }
    List<User> updateUsersDb = new ArrayList<>();
    boolean syncMobile = nonNull(directoryDb.getUserSchemaData().getMobileAttribute());
    for (User userLdap : usersLdap) {
      User userDb = usernameUserDbMap.get(userLdap.getUsername());
      // Updating other directory data is not allowed, must be same directory and entry
      if (nonNull(userDb) && directoryDb.getId().equals(userDb.getDirectoryId())) {
        // Important:: When the user source is LDAP_SYNCHRONIZE, the following fields on the page are not allowed to be updated, otherwise, the modification will be overwritten by LDAP synchronization
        // firstNameAttribute/lastNameAttribute/displayNameAttribute/emailAttribute/mobileAttribute/passdAttribute/passdEncoderType
        if (userLdap.notSameInDirectory(tenantDb, userDb, syncMobile)) {
          userDb.setFirstName(userLdap.getFirstName())
              .setLastName(userLdap.getLastName())
              .setFullName(userLdap.getFullName())
              .setUsername(userLdap.getUsername())
              .setEmail(userLdap.getEmail())
              .setMobile(userLdap.getMobile())
              .setPassword(userLdap.getPassword())
              .setTenantRealNameStatus(tenantDb.getRealNameStatus());
          userDb.setLastModifiedBy(longSafe(getUserId(), -1L /*By Job*/))
              .setLastModifiedDate(LocalDateTime.now());
          updateUsersDb.add(userDb);
        }
      }
    }
    return updateUsersDb;
  }

  private List<User> findDeleteUsersInDirectory(List<User> usersLdap,
      Map<String, User> usernameUserDbMap, UserDirectory directoryDb) {
    // Filter existed group in DB by directoryId
    // Deleting other directory data is not allowed
    List<User> existUsersInDb = usernameUserDbMap.values().stream()
        .filter(x -> directoryDb.getId().equals(x.getDirectoryId())).collect(Collectors.toList());
    if (isEmpty(usersLdap)) {
      return existUsersInDb;
    }

    List<User> deleteUsers = new ArrayList<>();
    Map<String, User> usernameUsersLdapMap = usersLdap.stream()
        .collect(Collectors.toMap(User::getUsername, x -> x));
    for (User existUser : existUsersInDb) {
      if (!usernameUsersLdapMap.containsKey(existUser.getUsername())) {
        deleteUsers.add(existUser);
      }
    }
    return deleteUsers;
  }

  /**
   * Verify Host and auth information
   */
  private void tryConnectLdapServer(DirectorySyncResult result, UserDirectory directoryDb,
      boolean onlyTest) {
    LDAPConnection ldapConnection = null;
    try {
      ldapConnection = openConnection(directoryDb.getServerData(), onlyTest);
    } catch (LDAPException e) {
      log.error("Retry ldap server exception: {}", e.getMessage());
      result.setErrorMessage(e.getMessage());
    } finally {
      if (nonNull(ldapConnection) && ldapConnection.isConnected()) {
        result.setConnectSuccess(true);
        ldapConnection.close();
      } else {
        result.setConnectSuccess(false);
      }
    }
  }

  private LDAPConnection openConnection(DirectoryServer server, boolean onlyTest)
      throws LDAPException {
    return new LDAPConnection(server.getHost(), server.getPort(), server.getUsername(),
        onlyTest ? server.getPassword() : decryptServerPassword(server.getPassword()));
  }

  /**
   * ldaps <a href="https://blog.csdn.net/wokoone/article/details/127843172">reference</a>
   */
  public LdapTemplate getLdapTemplate(UserDirectory directory, boolean onlyTest) {
    LdapContextSource contextSource = new LdapContextSource();
    contextSource.setBase(directory.getSchemaData().getBaseDn());
    String protocol = directory.getServerData().getSsl() ? "ldaps://" : "ldap://";
    contextSource.setUrl(protocol + directory.getServerData().getHost()
        + ":" + directory.getServerData().getPort());
    contextSource.setUserDn(directory.getServerData().getUsername());
    contextSource.setPassword(onlyTest ? directory.getServerData().getPassword()
        : decryptServerPassword(directory.getServerData().getPassword()));
    contextSource.afterPropertiesSet();
    return new LdapTemplate(contextSource);
  }

  private void calcDefaultSequence(UserDirectory directory) {
    Integer maxSequence = userDirectoryRepo.findMaxSequence();
    if (Objects.isNull(maxSequence)) {
      directory.setSequence(1);
    } else {
      directory.setSequence(maxSequence < MAX_VALUE ? maxSequence + 1 : MAX_VALUE);
    }
  }

  private String encryptServerPassword(String content) {
    Pair<String, String> passwordAndContent = Pair.of(new Str0(
        new long[]{0xE3F8C962FD1EBC09L, 0x2B9622D1AFD4BAFBL, 0x5995E6A31CA7BCA2L,
            0xCC1324E31C4F4D7L, 0x27D888692A91D511L})
        .toString() /* => "gE1rD5nW5wG6gA8nJ7mE5pU9aL5pB6pI" */, content);
    return AESUtils.encrypt(passwordAndContent);
  }

  private String decryptServerPassword(String encrypted) {
    Pair<String, String> passwordAndEncrypted = Pair.of(new Str0(
        new long[]{0xE06AD76F1A38E7F2L, 0xAD2E9D08B20EBBB5L, 0x818203C030C715F3L,
            0x261E23A6D9AE749L, 0x59FD2AC1A12E018AL})
        .toString() /* => "gE1rD5nW5wG6gA8nJ7mE5pU9aL5pB6pI" */, encrypted);
    return AESUtils.decrypt(passwordAndEncrypted);
  }

  @Override
  protected BaseRepository<UserDirectory, Long> getRepository() {
    return this.userDirectoryRepo;
  }
}
