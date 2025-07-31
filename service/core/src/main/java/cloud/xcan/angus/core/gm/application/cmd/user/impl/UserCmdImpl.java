package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.api.enums.UserSource.LDAP_SYNCHRONIZE;
import static cloud.xcan.angus.api.enums.UserSource.isNewSignup;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.assembleUserInfo;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.replaceToAuthUser;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.setUserMainDeptAndHead;
import static cloud.xcan.angus.core.gm.domain.UserMessage.USER_REFUSE_OPERATE_ADMIN;
import static cloud.xcan.angus.core.gm.domain.UserMessage.USER_SYS_ADMIN_NUM_ERROR;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CANCEL_SYS_ADMIN;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.LOCKED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SET_SYS_ADMIN;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SIGN_UP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UNLOCKED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
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
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserCmd;
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

/**
 * <p>
 * Implementation of user command operations.
 * </p>
 * <p>
 * Manages user lifecycle including creation, updates, deletion, status management,
 * and lock/unlock operations.
 * </p>
 * <p>
 * Supports comprehensive user management with department, group, and tag associations,
 * OAuth2 integration, and audit logging.
 * </p>
 */
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

  /**
   * <p>
   * Creates a new user with comprehensive validation and associations.
   * </p>
   * <p>
   * Validates user information, tenant initialization, quota limits, and duplicate checks.
   * Creates user with department, group, and tag associations, and initializes OAuth2 user.
   * </p>
   * <p>
   * Supports different user sources including platform signup, invitation code signup,
   * background signup, and LDAP synchronization.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags, UserSource userSource) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Tenant optTenant = null;

      @Override
      protected void checkParams() {
        // Verify and initialize tenant
        optTenant = checkAndInitTenant(user, userSource);

        // Verify required account information
        // Allow LDAP user's mobile and email to be empty
        assertTrue(LDAP_SYNCHRONIZE.equals(userSource)
                || isNotEmpty(user.getEmail()) || isNotEmpty(user.getMobile()),
            "Mobile and email cannot be all empty");

        // Verify account for new tenant signup
        if (UserSource.isNewSignup(userSource)) {
          userQuery.checkSignupAccounts(user);
        }

        // Verify user account for existing tenant
        if (UserSource.isAddUser(userSource)) {
          // Verify user quota
          userQuery.checkAddQuota(optTenant, user, deptUsers, groupUsers, userTags);

          // Verify username uniqueness across platform
          userQuery.checkAddUsername(user.getUsername());
          // userQuery#checkUsernames() will turn off multiTenantCtrl

          // Verify mobile uniqueness within tenant
          userQuery.checkAddMobile(user.getMobile(), userSource);

          // Verify email uniqueness within tenant
          userQuery.checkAddEmail(user.getEmail(), userSource);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add associated targets when added in background
        addAssociateTarget(deptUsers, groupUsers, userTags);

        // Set user tenant name
        user.setTenantName(optTenant.getName());
        // Set user's main department
        setUserMainDeptAndHead(deptUsers, user);

        // Save user
        IdKey<Long, Object> idKeys = insert(user);

        // Initialize tenant or user settings
        settingUserCmd.tenantAndUserInit(optTenant.getId(), isNewSignup(user.getSource()),
            user.getId());

        // Initialize OAuth2 user
        authUserCmd.replace0(replaceToAuthUser(user, user.getPassword(), optTenant),
            isNewSignup(userSource));

        // Log operation for audit
        if (!isUserAction()) {
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

  /**
   * <p>
   * Updates user information and associated data.
   * </p>
   * <p>
   * Validates user existence and uniqueness constraints, updates user information,
   * and manages associated department, group, and tag relationships.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Verify user exists
        userDb = userQuery.checkAndFind(user.getId());

        // Verify username uniqueness
        if (isNotEmpty(user.getUsername()) && !user.getUsername().equals(userDb.getUsername())) {
          userQuery.checkUsernameUpdate(user.getUsername(), userDb.getId());
        }

        // Verify email uniqueness
        if (isNotEmpty(user.getEmail()) && !user.getEmail().equals(userDb.getEmail())) {
          userQuery.checkUpdateEmail(user.getEmail(), user.getId());
        }

        // Verify mobile uniqueness
        if (isNotEmpty(user.getMobile()) && !user.getMobile().equals(userDb.getMobile())) {
          userQuery.checkUpdateMobile(user.getMobile(), user.getId());
        }
      }

      @Override
      protected Void process() {
        // Update associated targets
        updateAssociateTarget(user.getId(), deptUsers, groupUsers, userTags);

        // Set user's main department
        setUserMainDeptAndHead(deptUsers, userDb);

        // Save user
        userRepo.save(copyPropertiesIgnoreNull(user, userDb));

        // Update OAuth2 user
        authUserCmd.replaceAuthUser(userDb, user.getPassword(), false);

        // Log operation for audit
        operationLogCmd.add(USER, userDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces user information completely.
   * </p>
   * <p>
   * Handles both new user creation and existing user updates with comprehensive validation.
   * Manages user associations and OAuth2 integration.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(User user, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    return new BizTemplate<IdKey<Long, Object>>() {
      User userDb;

      @Override
      protected void checkParams() {
        if (nonNull(user.getId())) {
          // Verify user exists
          userDb = userQuery.checkAndFind(user.getId());

          // Verify required account information
          assertTrue(isNotEmpty(user.getEmail()) || isNotEmpty(user.getMobile()),
              "Mobile and email address cannot be all empty");
          assertNotEmpty(!userDb.getSignupAccountType().isEmail()
                  || userDb.getEmail().equals(user.getEmail()),
              "The registered email cannot be modified");
          assertNotEmpty(!userDb.getSignupAccountType().isMobile()
                  || userDb.getMobile().equals(user.getMobile()),
              "The registered mobile cannot be modified");

          // Verify username uniqueness
          userQuery.checkUsernameUpdate(user.getUsername(), userDb.getId());

          // Verify email uniqueness
          if (isNotEmpty(user.getEmail()) && !user.getEmail().equals(userDb.getEmail())) {
            userQuery.checkUpdateEmail(user.getEmail(), user.getId());
          }

          // Verify mobile uniqueness
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

        // Replace associated targets
        replaceAssociateTarget(user.getId(), deptUsers, groupUsers, userTags);

        // Set user's main department
        setUserMainDeptAndHead(deptUsers, userDb);

        // Assemble updated user information
        assembleUserInfo(userDb, user);

        // Save user
        userRepo.save(userDb);

        // Update OAuth2 user
        Tenant tenantDb = tenantQuery.checkAndFind(userDb.getTenantId());
        authUserCmd.replace0(replaceToAuthUser(userDb, user.getPassword(), tenantDb), false);

        // Log operation for audit
        operationLogCmd.add(USER, userDb, UPDATED);
        return IdKey.of(userDb.getId(), userDb.getFullName());
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes users and associated data.
   * </p>
   * <p>
   * Validates administrator permissions and ensures at least one system administrator remains.
   * Removes user associations and OAuth2 authorizations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Verify users exist
        usersDb = userQuery.checkAndFind(ids);
        // Verify administrator operation permissions
        userQuery.checkRefuseOperateAdmin(usersDb);
        // Verify at least one system administrator remains
        assertTrue(userQuery.countValidSysAdminUser() > ids.size(), USER_SYS_ADMIN_NUM_ERROR);
      }

      @Override
      protected Void process() {
        // Delete associated targets
        deleteUserAssociateRelation(ids);

        // Delete users
        userRepo.deleteByIdIn(ids);

        // Delete OAuth2 users
        authUserCmd.delete(ids);

        // Delete OAuth2 authorizations to force user logout
        authUserCmd.deleteAuthorization(usersDb.stream().map(User::getUsername).toList());

        // Log operation for audit
        operationLogCmd.addAll(USER, usersDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Enables or disables users.
   * </p>
   * <p>
   * Updates user status and ensures at least one system administrator remains.
   * Manages OAuth2 user status and forces disabled users to logout.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<User> users) {
    new BizTemplate<Void>() {
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Verify users exist
        usersDb = userQuery.checkAndFind(users.stream().map(User::getId)
            .collect(Collectors.toList()));
        // Verify administrator operation permissions
        userQuery.checkRefuseOperateAdmin(usersDb);
      }

      @Override
      protected Void process() {
        // Update user enabled status
        Map<Long, User> usersMap = users.stream().collect(Collectors.toMap(User::getId, x -> x));
        for (User userDb : usersDb) {
          userDb.setEnabled(usersMap.get(userDb.getId()).getEnabled());
        }
        batchUpdate0(usersDb);

        // Verify at least one system administrator remains
        assertTrue(userQuery.countValidSysAdminUser() > 0, USER_SYS_ADMIN_NUM_ERROR);

        // Update OAuth2 user status
        for (User userDb : usersDb) {
          authUserCmd.replaceAuthUser(userDb, null, false);
        }

        // Delete OAuth2 authorizations for disabled users to force logout
        List<User> disabledUsers = usersDb.stream().filter(x -> !x.getEnabled()).toList();
        if (isNotEmpty(disabledUsers)){
          authUserCmd.deleteAuthorization(disabledUsers.stream().map(User::getUsername).toList());
        }

        // Log operations for audit
        operationLogCmd.addAll(USER, usersDb.stream().filter(User::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(USER, disabledUsers , DISABLED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Locks or unlocks users with optional date constraints.
   * </p>
   * <p>
   * Validates date constraints and administrator permissions, updates lock status,
   * and manages OAuth2 authorizations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void locked(Long userId, Boolean locked, LocalDateTime lockStartDate,
      LocalDateTime lockEndDate) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Verify date constraints are valid
        assertTrue(!locked || isNull(lockStartDate)
                || isNull(lockEndDate) || lockEndDate.isAfter(lockStartDate),
            String.format("lockEndDate[%s] is not after lockStartDate[%s]",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null,
                nonNull(lockStartDate) ? lockStartDate.format(DATE_TIME_FMT) : null));
        assertTrue(!locked || isNull(lockEndDate) // Ignore warning??
                || lockEndDate.isAfter(LocalDateTime.now()),
            String.format("lockEndDate[%s] must be a future date",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null));
        // Verify user exists
        userDb = userQuery.checkAndFind(userId);

        // Verify administrator operation permissions
        userQuery.checkRefuseOperateAdmin(Collections.singletonList(userDb));

        // Verify at least one system administrator remains
        assertTrue(!locked || !userDb.getSysAdmin()
            || userQuery.countValidSysAdminUser() > 1, USER_SYS_ADMIN_NUM_ERROR);
      }

      @Override
      protected Void process() {
        // Update user lock status
        if (locked) {
          userDb.setLocked((isNull(lockStartDate) && isNull(lockEndDate))
                  || (nonNull(lockStartDate) && lockStartDate
                  .isBefore(LocalDateTime.now().minusSeconds(30))))
              .setLockStartDate(lockStartDate).setLockEndDate(lockEndDate);
        } else {
          userDb.setLocked(false).setLockStartDate(null).setLockEndDate(null);
        }
        userRepo.save(userDb);

        // Update OAuth2 user lock status
        authUserCmd.replaceAuthUser(userDb, null, false);

        // Delete OAuth2 authorization to force user logout when locked
        if (locked){
          authUserCmd.deleteAuthorization(List.of(userDb.getUsername()));
        }

        // Log operation for audit
        operationLogCmd.add(USER, userDb, locked ? LOCKED : UNLOCKED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Sets or cancels system administrator status for users.
   * </p>
   * <p>
   * Validates administrator permissions and ensures at least one system administrator remains.
   * Updates user system administrator status and OAuth2 user information.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void adminSet(Long userId, Boolean sysAdmin) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Verify system administrator permissions
        assertForbidden(isTenantSysAdmin() || isToUser(), USER_REFUSE_OPERATE_ADMIN, FORBIDDEN_KEY);
        // Verify user exists
        userDb = userQuery.checkAndFind(userId);
        // Verify at least one system administrator remains
        assertTrue(sysAdmin || userQuery.countValidSysAdminUser() > 1,
            USER_SYS_ADMIN_NUM_ERROR);
      }

      @Override
      protected Void process() {
        userDb.setSysAdmin(sysAdmin);
        userRepo.save(userDb);

        authUserCmd.replaceAuthUser(userDb, null, false);

        // Log operation for audit
        operationLogCmd.add(USER, userDb, sysAdmin ? SET_SYS_ADMIN : CANCEL_SYS_ADMIN);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Processes expired user locks.
   * </p>
   * <p>
   * Used by UserLockJob to automatically lock users when lock conditions are met.
   * </p>
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
   * <p>
   * Processes expired user unlocks.
   * </p>
   * <p>
   * Used by UserUnlockJob to automatically unlock users when unlock conditions are met.
   * </p>
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

  /**
   * <p>
   * Deletes directory users and associated data.
   * </p>
   * <p>
   * Removes users, associated relationships, and OAuth2 users.
   * Ensures at least one system administrator remains.
   * </p>
   */
  @Override
  public void deleteDirectoryUsers(Set<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }

    // Delete user accounts
    userRepo.deleteAllByIdIn(userIds);

    // Verify at least one system administrator remains
    assertTrue(userQuery.countValidSysAdminUser() > 0, USER_SYS_ADMIN_NUM_ERROR);

    // Delete associated targets
    userDeptCmd.deleteByUserId(userIds);
    userGroupCmd.deleteByUserId(userIds);
    orgTagTargetCmd.deleteAllByTarget(OrgTargetType.USER, userIds);

    // Delete OAuth2 users
    authUserCmd.delete(userIds);
  }

  /**
   * <p>
   * Empties directory users without deletion.
   * </p>
   * <p>
   * Marks directory users as empty without removing them from the system.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void emptyDirectoryUsers(Set<Long> userIds) {
    userRepo.updateDirectoryEmptyByUserIdIn(userIds);
  }

  /**
   * <p>
   * Empties all users in a specific directory.
   * </p>
   * <p>
   * Marks all users in the specified directory as empty.
   * </p>
   */
  @Override
  public void emptyDirectoryUsers(Long directoryId) {
    userRepo.updateDirectoryEmptyByDirectoryId(directoryId);
  }

  /**
   * <p>
   * Clears main department for users in specified departments.
   * </p>
   * <p>
   * Removes main department assignment for users in the specified departments.
   * </p>
   */
  @Override
  public void clearMainDeptByDeptIdIn(Set<Long> deptIds) {
    userRepo.updateMainDeptByDeptIdIn(deptIds);
  }

  /**
   * <p>
   * Clears main department for specific user and departments.
   * </p>
   * <p>
   * Removes main department assignment for specific user in specified departments.
   * </p>
   */
  @Override
  public void clearMainDeptByUserIdAndDeptIdIn(Long userId, Set<Long> deptIds) {
    userRepo.updateMainDeptByUserIdAndDeptIdIn(userId, deptIds);
  }

  /**
   * <p>
   * Clears main department for users in specific department.
   * </p>
   * <p>
   * Removes main department assignment for users in the specified department.
   * </p>
   */
  @Override
  public void clearMainDeptByDeptIdAndUserIdIn(Long deptId, Set<Long> userIds) {
    userRepo.updateMainDeptByDeptIdAndUserIdIn(deptId, userIds);
  }

  /**
   * <p>
   * Deletes users by tenant IDs.
   * </p>
   * <p>
   * Removes all users and associated data for the specified tenants.
   * </p>
   */
  @Override
  public void deleteByTenant(Set<Long> tenantIds) {
    deleteTenantAssociateRelation(tenantIds);
    // Delete user account information
    userRepo.deleteByTenantIdIn(tenantIds);

    // NOOP: authUserRemote.deleteByTenantId(new HashSet<>(tenantIds));
  }

  /**
   * <p>
   * Deletes users by directory with optional synchronization.
   * </p>
   * <p>
   * Deletes or empties users based on directory configuration and synchronization settings.
   * </p>
   */
  @Override
  public void deleteByDirectory(Long id, boolean deleteSync) {
    if (deleteSync) {
      deleteDirectoryUsers(userRepo.findUserIdsByDirectoryId(id));
    } else {
      emptyDirectoryUsers(id);
    }
  }

  /**
   * <p>
   * Validates and initializes invitation tenant.
   * </p>
   * <p>
   * Verifies invitation code and retrieves associated tenant information.
   * </p>
   */
  private Tenant checkAndInitInvitationTenant(User user) {
    String invitationCode = user.getInvitationCode();
    assertNotEmpty(invitationCode, "invitationCode is required");
    SettingTenant settingTenant = settingTenantManager.checkAndFindSetting(invitationCode);
    Tenant tenant = tenantQuery.detail(settingTenant.getTenantId());
    user.setTenantId(tenant.getId());
    return tenant;
  }

  /**
   * <p>
   * Validates and initializes tenant based on user source.
   * </p>
   * <p>
   * Handles different tenant initialization scenarios based on user source type.
   * </p>
   */
  public Tenant checkAndInitTenant(User user, UserSource userSource) {
    Tenant optTenant;
    if (UserSource.PLATFORM_SIGNUP.equals(userSource)) {
      optTenant = initPlatformSignupTenant(user);
    } else if (UserSource.INVITATION_CODE_SIGNUP.equals(userSource)) {
      optTenant = checkAndInitInvitationTenant(user);
      // Important: Enable multi tenant controller <- Invoke by /innerapi
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
    // Set tenant context
    PrincipalContext.get().setTenantId(optTenant.getId());
    PrincipalContext.get().setTenantName(optTenant.getName());
    user.setTenantId(nullSafe(user.getTenantId(), optTenant.getId()));
    return optTenant;
  }

  /**
   * <p>
   * Initializes platform signup tenant.
   * </p>
   * <p>
   * Creates new tenant for platform signup users.
   * </p>
   */
  private Tenant initPlatformSignupTenant(User user) {
    // Create signup tenant
    Tenant signupTenant = TenantConverter.toSignupTenant(TenantSource.PLATFORM_SIGNUP);
    tenantCmd.add0(signupTenant);
    user.setTenantId(signupTenant.getId());
    return signupTenant;
  }

  /**
   * <p>
   * Adds associated targets for user.
   * </p>
   * <p>
   * Creates department, group, and tag associations for the user.
   * </p>
   */
  private void addAssociateTarget(List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    long optTenantId = getOptTenantId();
    if (isNotEmpty(deptUsers)) {
      userDeptCmd.add0(deptUsers.stream().peek(x -> {
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

  /**
   * <p>
   * Updates associated targets for user.
   * </p>
   * <p>
   * Replaces department, group, and tag associations for the user.
   * </p>
   */
  private void updateAssociateTarget(Long userId, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    long optTenantId = getOptTenantId();
    if (isNotEmpty(deptUsers)) {
      userDeptCmd.deleteByUserId(Collections.singleton(userId));
      userDeptCmd.add0(deptUsers.stream().peek(x -> {
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

  /**
   * <p>
   * Replaces associated targets for user.
   * </p>
   * <p>
   * Clears existing associations and creates new ones.
   * </p>
   */
  private void replaceAssociateTarget(Long userId, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    // Replace -> Clear existing associations
    deleteUserAssociateRelation(Collections.singleton(userId));

    // Add new associations
    addAssociateTarget(deptUsers, groupUsers, userTags);
  }

  /**
   * <p>
   * Deletes user associated relationships.
   * </p>
   * <p>
   * Removes department, group, and tag associations for specified users.
   * </p>
   */
  private void deleteUserAssociateRelation(Set<Long> userIds) {
    if (isNotEmpty(userIds)) {
      userDeptCmd.deleteByUserId(userIds);
      userGroupCmd.deleteByUserId(userIds);
      orgTagTargetCmd.deleteAllByTarget(OrgTargetType.USER, userIds);
    }
  }

  /**
   * <p>
   * Deletes tenant associated relationships.
   * </p>
   * <p>
   * Removes department, group, and tag associations for specified tenants.
   * </p>
   */
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
