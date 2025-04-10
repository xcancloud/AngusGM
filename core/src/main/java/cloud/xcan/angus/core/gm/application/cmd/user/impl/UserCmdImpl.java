package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.api.enums.UserSource.LDAP_SYNCHRONIZE;
import static cloud.xcan.angus.api.enums.UserSource.isNewSignup;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.assembleUserInfo;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.replaceToAuthUser;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.setUserMainDeptAndHead;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.USER_REFUSE_OPERATE_ADMIN;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.USER_SYS_ADMIN_NUM_ERROR;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SIGN_UP;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isToUser;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.FORBIDDEN_KEY;
import static cloud.xcan.angus.spec.utils.DateUtils.DATE_TIME_FMT;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.TenantSource;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.manager.SettingTenantManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.converter.TenantConverter;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.infra.job.UserLockJob;
import cloud.xcan.angus.core.gm.infra.job.UserUnlockJob;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Biz
public class UserCmdImpl extends CommCmd<User, Long> implements UserCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserQuery userQuery;

  @Resource
  private DeptUserCmd userDeptCmd;

  @Resource
  private GroupUserCmd userGroupCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private TenantCmd tenantCmd;

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private SettingTenantManager settingTenantManager;

  @Resource
  private AuthUserCmd authUserCmd;

  @Resource
  private SettingUserCmd settingUserCmd;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags, UserSource userSource) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Tenant optTenant = null;

      @Override
      protected void checkParams() {
        // Check and init tenant
        optTenant = checkAndInitTenant(user, userSource);

        // Check the required account
        // Allow LDAP user's mobile and email to be empty
        assertTrue(LDAP_SYNCHRONIZE.equals(userSource)
                || isNotEmpty(user.getEmail()) || isNotEmpty(user.getMobile()),
            "Mobile and email cannot be all empty");

        // Check the account if new tenant signup
        if (UserSource.isNewSignup(userSource)) {
          userQuery.checkSignupAccounts(user);
        }

        // Add the user account if tenant existed
        if (UserSource.isAddUser(userSource)) {
          // Check the user quota
          userQuery.checkAddQuota(optTenant, user, deptUsers, groupUsers, userTags);

          // Check the duplicated username under the platform
          userQuery.checkAddUsername(user.getUsername());
          // userQuery#checkUsernames() will turn off multiTenantCtrl

          // Check the duplicated mobile under the tenant
          userQuery.checkAddMobile(user.getMobile(), userSource);

          // Check the duplicated email under the tenant
          userQuery.checkAddEmail(user.getEmail(), userSource);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add associate targets when added in the background
        addAssociateTarget(deptUsers, groupUsers, userTags);

        // Set user tenant name
        user.setTenantName(optTenant.getName());
        // Set the main dept of user
        setUserMainDeptAndHead(deptUsers, user);

        // Save user
        IdKey<Long, Object> idKeys = insert(user);

        // Initialize tenant or user setting
        settingUserCmd.tenantAndUserInit(optTenant.getId(),
            UserSource.isNewSignup(user.getSource()), user.getId());

        // Initialize the oauth2 user
        authUserCmd.replace0(replaceToAuthUser(user, optTenant), isNewSignup(userSource));

        // Save operation log
        if (!isUserAction()){
          PrincipalContext.get().setUserId(user.getId()).setFullName(user.getFullName())
              .setTenantId(user.getTenantId()).setTenantName(user.getTenantName());
        }
        if (user.getSource().isPlatformSignup()) {
          operationLogCmd.add(USER, user, SIGN_UP);
        } else {
          operationLogCmd.add(USER, user, CREATED);
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        userDb = userQuery.checkAndFind(user.getId());

        // Check the username is not duplicated
        if (isNotEmpty(user.getUsername()) && !user.getUsername().equals(userDb.getUsername())) {
          userQuery.checkUsernameUpdate(user.getUsername(), userDb.getId());
        }

        // Check the email is not duplicated
        if (isNotEmpty(user.getEmail()) && !user.getEmail().equals(userDb.getEmail())) {
          userQuery.checkUpdateEmail(user.getEmail(), user.getId());
        }

        // Check the mobile is not duplicated
        if (isNotEmpty(user.getMobile()) && !user.getMobile().equals(userDb.getMobile())) {
          userQuery.checkUpdateMobile(user.getMobile(), user.getId());
        }
      }

      @Override
      protected Void process() {
        // Update associate targets
        updateAssociateTarget(user.getId(), deptUsers, groupUsers, userTags);

        // Set the main dept of user
        setUserMainDeptAndHead(deptUsers, userDb);

        // Save user
        userRepo.save(copyPropertiesIgnoreNull(user, userDb));

        // Update oauth2 user
        Tenant tenantDb = tenantQuery.checkAndFind(user.getTenantId());
        authUserCmd.replace0(replaceToAuthUser(userDb, tenantDb), false);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(User user, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    return new BizTemplate<IdKey<Long, Object>>() {
      User userDb;

      @Override
      protected void checkParams() {
        if (nonNull(user.getId())) {
          // Check the user existed
          userDb = userQuery.checkAndFind(user.getId());

          // Check the account required
          assertTrue(isNotEmpty(user.getEmail()) || isNotEmpty(user.getMobile()),
              "Mobile and email address cannot be all empty");
          assertNotEmpty(!userDb.getSignupAccountType().isEmail()
                  || userDb.getEmail().equals(user.getEmail()),
              "The registered email cannot be modified");
          assertNotEmpty(!userDb.getSignupAccountType().isMobile()
                  || userDb.getMobile().equals(user.getMobile()),
              "The registered mobile cannot be modified");

          // Check the username is not duplicated
          userQuery.checkUsernameUpdate(user.getUsername(), userDb.getId());

          // Check the email is not duplicated
          if (isNotEmpty(user.getEmail()) && !user.getEmail().equals(userDb.getEmail())) {
            userQuery.checkUpdateEmail(user.getEmail(), user.getId());
          }

          // Check the  mobile is not duplicated
          if (isNotEmpty(user.getMobile()) && !user.getMobile().equals(userDb.getMobile())) {
            userQuery.checkUpdateMobile(user.getMobile(), user.getId());
          }
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(user.getId())) {
          return add(user, deptUsers, groupUsers, userTags, UserSource.BACKGROUND_ADDED);
        }

        // Replace associate targets
        replaceAssociateTarget(user.getId(), deptUsers, groupUsers, userTags);

        // Set the main dept of user
        setUserMainDeptAndHead(deptUsers, userDb);

        // Set update user info
        assembleUserInfo(userDb, user);

        // Save user
        userRepo.save(userDb);

        // Update oauth2 user
        Tenant tenantDb = tenantQuery.checkAndFind(userDb.getTenantId());
        authUserCmd.replace0(replaceToAuthUser(userDb, tenantDb), false);
        return IdKey.of(userDb.getId(), userDb.getFullName());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        usersDb = userQuery.checkAndFind(ids);
        // Check the delete admin permission
        userQuery.checkRefuseOperateAdmin(usersDb);
        // Check there is at least one system administrator
        assertTrue(userQuery.countValidSysAdminUser() > ids.size(), USER_SYS_ADMIN_NUM_ERROR);
      }

      @Override
      protected Void process() {
        // Delete associate targets
        deleteUserAssociateRelation(ids);

        // Delete the user
        userRepo.deleteByIdIn(ids);

        // Delete oauth2 user
        authUserCmd.delete(ids);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<User> users) {
    new BizTemplate<Void>() {
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        usersDb = userQuery.checkAndFind(users.stream().map(User::getId)
            .collect(Collectors.toList()));
        // Check the enable or disable admin permission
        userQuery.checkRefuseOperateAdmin(usersDb);
      }

      @Override
      protected Void process() {
        Map<Long, User> usersMap = users.stream().collect(Collectors.toMap(User::getId, x -> x));
        for (User userDb : usersDb) {
          userDb.setEnabled(usersMap.get(userDb.getId()).getEnabled());
        }
        batchUpdate0(usersDb);

        // Check there is at least one system administrator
        assertTrue(userQuery.countValidSysAdminUser() > 0, USER_SYS_ADMIN_NUM_ERROR);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void locked(Long userId, Boolean locked, LocalDateTime lockStartDate,
      LocalDateTime lockEndDate) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Check the date is valid
        assertTrue(!locked || isNull(lockStartDate)
                || isNull(lockEndDate) || lockEndDate.isAfter(lockStartDate),
            String.format("lockEndDate[%s] is not after lockStartDate[%s]",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null,
                nonNull(lockStartDate) ? lockStartDate.format(DATE_TIME_FMT) : null));
        assertTrue(!locked || isNull(lockEndDate) // Ignore warning??
                || lockEndDate.isAfter(LocalDateTime.now()),
            String.format("lockEndDate[%s] must is a future date",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null));
        // Check the user existed
        userDb = userQuery.checkAndFind(userId);

        // Check the lock or unlock admin permission
        userQuery.checkRefuseOperateAdmin(Collections.singletonList(userDb));

        // Check there is at least one system administrator
        assertTrue(!locked || !userDb.getSysAdmin()
            || userQuery.countValidSysAdminUser() > 1, USER_SYS_ADMIN_NUM_ERROR);
      }

      @Override
      protected Void process() {
        if (locked) {
          userDb.setLocked((isNull(lockStartDate) && isNull(lockEndDate))
                  || (nonNull(lockStartDate) && lockStartDate
                  .isBefore(LocalDateTime.now().minusSeconds(30))))
              .setLockStartDate(lockStartDate).setLockEndDate(lockEndDate);
        } else {
          userDb.setLocked(false).setLockStartDate(null).setLockEndDate(null);
        }
        userRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void adminSet(Long userId, Boolean sysAdmin) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Check the system admin permission
        assertForbidden(isTenantSysAdmin() || isToUser(), USER_REFUSE_OPERATE_ADMIN, FORBIDDEN_KEY);
        // Check the user existed
        userDb = userQuery.checkAndFind(userId);
        // Check there is at least one system administrator
        assertTrue(sysAdmin || userQuery.countValidSysAdminUser() > 1,
            USER_SYS_ADMIN_NUM_ERROR);
      }

      @Override
      protected Void process() {
        userDb.setSysAdmin(sysAdmin);
        userRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  /**
   * User by {@link UserLockJob}
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void lockExpire(Set<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (isNotEmpty(userIds)) {
          userRepo.updateLockStatusByIdIn(userIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * User by {@link UserUnlockJob}
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void unlockExpire(Set<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (isNotEmpty(userIds)) {
          userRepo.updateUnlockStatusByIdIn(userIds);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteDirectoryUsers(Set<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }

    // Delete user account
    userRepo.deleteAllByIdIn(userIds);

    // Check there is at least one system administrator
    assertTrue(userQuery.countValidSysAdminUser() > 0, USER_SYS_ADMIN_NUM_ERROR);

    // Delete associate targets
    userDeptCmd.deleteByUserId(userIds);
    userGroupCmd.deleteByUserId(userIds);
    orgTagTargetCmd.deleteAllByTarget(OrgTargetType.USER, userIds);

    // Delete oauth2 user
    authUserCmd.delete(userIds);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void emptyDirectoryUsers(Set<Long> userIds) {
    userRepo.updateDirectoryEmptyByUserIdIn(userIds);
  }

  @Override
  public void emptyDirectoryUsers(Long directoryId) {
    userRepo.updateDirectoryEmptyByDirectoryId(directoryId);
  }

  @Override
  public void clearMainDeptByDeptIdIn(Set<Long> deptIds) {
    userRepo.updateMainDeptByDeptIdIn(deptIds);
  }

  @Override
  public void clearMainDeptByUserIdAndDeptIdIn(Long userId, Set<Long> deptIds) {
    userRepo.updateMainDeptByUserIdAndDeptIdIn(userId, deptIds);
  }

  @Override
  public void clearMainDeptByDeptIdAndUserIdIn(Long deptId, Set<Long> userIds) {
    userRepo.updateMainDeptByDeptIdAndUserIdIn(deptId, userIds);
  }

  @Override
  public void deleteByTenant(Set<Long> tenantIds) {
    deleteTenantAssociateRelation(tenantIds);
    // Delete user account info
    userRepo.deleteByTenantIdIn(tenantIds);

    // NOOP:: authUserRemote.deleteByTenantId(new HashSet<>(tenantIds));
  }

  @Override
  public void deleteByDirectory(Long id, boolean deleteSync) {
    if (deleteSync) {
      deleteDirectoryUsers(userRepo.findUserIdsByDirectoryId(id));
    } else {
      emptyDirectoryUsers(id);
    }
  }

  private Tenant checkAndInitInvitationTenant(User user) {
    String invitationCode = user.getInvitationCode();
    assertNotEmpty(invitationCode, "invitationCode is required");
    SettingTenant settingTenant = settingTenantManager.checkAndFindSetting(invitationCode);
    Tenant tenant = tenantQuery.detail(settingTenant.getTenantId());
    user.setTenantId(tenant.getId());
    return tenant;
  }

  public Tenant checkAndInitTenant(User user, UserSource userSource) {
    Tenant optTenant;
    if (UserSource.PLATFORM_SIGNUP.equals(userSource)) {
      optTenant = initPlatformSignupTenant(user);
    } else if (UserSource.INVITATION_CODE_SIGNUP.equals(userSource)) {
      optTenant = checkAndInitInvitationTenant(user);
      // Important:: Enable multi tenant controller <- Invoke by /innerapi
      setMultiTenantCtrl(true);
    } else if (UserSource.BACKGROUND_SIGNUP.equals(userSource)) {
      // See TenantCmd#add()
      optTenant = tenantQuery.detail(user.getTenantId());
    } else if (UserSource.BACKGROUND_ADDED.equals(userSource)) {
      optTenant = tenantQuery.detail(nullSafe(user.getTenantId(), getOptTenantId()));
    } else {
      // THIRD_PARTY_LOGIN, LDAP_SYNCHRONIZE
      optTenant = tenantQuery.detail(user.getTenantId());
    }
    // Set tenant
    PrincipalContext.get().setTenantId(optTenant.getId());
    PrincipalContext.get().setTenantName(optTenant.getName());
    user.setTenantId(nullSafe(user.getTenantId(), optTenant.getId()));
    return optTenant;
  }

  private Tenant initPlatformSignupTenant(User user) {
    //Signup tenant
    Tenant signupTenant = TenantConverter.toSignupTenant(TenantSource.PLATFORM_SIGNUP);
    tenantCmd.add0(signupTenant);
    user.setTenantId(signupTenant.getId());
    return signupTenant;
  }

  private void addAssociateTarget(List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    long optTenantId = getOptTenantId();
    if (isNotEmpty(deptUsers)) {
      userDeptCmd.add(deptUsers.stream().peek(x -> {
        x.setTenantId(optTenantId); // Inject for job or innerapi
      }).collect(Collectors.toList()));
    }

    if (isNotEmpty(groupUsers)) {
      userGroupCmd.add(groupUsers.stream().peek(x -> {
        x.setTenantId(optTenantId); // Inject for job or innerapi
      }).collect(Collectors.toList()));
    }

    if (isNotEmpty(userTags)) {
      orgTagTargetCmd.add(userTags.stream().peek(x -> {
        x.setTenantId(optTenantId); // Inject for job or innerapi
      }).collect(Collectors.toList()));
    }
  }

  private void updateAssociateTarget(Long userId, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    long optTenantId = getOptTenantId();
    if (isNotEmpty(deptUsers)) {
      userDeptCmd.deleteByUserId(Collections.singleton(userId));
      userDeptCmd.add(deptUsers.stream().peek(x -> {
        x.setTenantId(optTenantId); // Inject for job or innerapi
      }).collect(Collectors.toList()));
    }

    if (isNotEmpty(groupUsers)) {
      userGroupCmd.deleteByUserId(Collections.singleton(userId));
      userGroupCmd.add(groupUsers.stream().peek(x -> {
        x.setTenantId(optTenantId); // Inject for job or innerapi
      }).collect(Collectors.toList()));
    }

    if (isNotEmpty(userTags)) {
      orgTagTargetCmd.deleteAllByTarget(OrgTargetType.USER, Collections.singleton(userId));
      orgTagTargetCmd.add(userTags.stream().peek(x -> {
        x.setTenantId(optTenantId); // Inject for job or innerapi
      }).collect(Collectors.toList()));
    }
  }

  private void replaceAssociateTarget(Long userId, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    // Replace -> Clear the empty of association
    deleteUserAssociateRelation(Collections.singleton(userId));

    // Add new association
    addAssociateTarget(deptUsers, groupUsers, userTags);
  }

  private void deleteUserAssociateRelation(Set<Long> userIds) {
    if (isNotEmpty(userIds)) {
      userDeptCmd.deleteByUserId(userIds);
      userGroupCmd.deleteByUserId(userIds);
      orgTagTargetCmd.deleteAllByTarget(OrgTargetType.USER, userIds);
    }
  }

  private void deleteTenantAssociateRelation(Set<Long> tenantIds) {
    if (isNotEmpty(tenantIds)) {
      userDeptCmd.deleteByTenantId(tenantIds);
      userGroupCmd.deleteByTenantId(tenantIds);
      orgTagTargetCmd.deleteAllByTenantId(tenantIds);
    }
  }

  @Override
  protected BaseRepository<User, Long> getRepository() {
    return this.userRepo;
  }
}
