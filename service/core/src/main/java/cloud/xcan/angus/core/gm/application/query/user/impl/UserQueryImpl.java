package cloud.xcan.angus.core.gm.application.query.user.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.gm.domain.UserMessage.USER_ACCOUNT_EXISTED_ERROR;
import static cloud.xcan.angus.core.gm.domain.UserMessage.USER_REFUSE_OPERATE_ADMIN;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isMultiTenantCtrl;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isToUser;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.FORBIDDEN_KEY;
import static cloud.xcan.angus.remote.message.http.ResourceExisted.M.RESOURCE_ALREADY_EXISTS_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.SneakyThrow0;
import cloud.xcan.angus.core.gm.application.converter.UserConverter;
import cloud.xcan.angus.core.gm.application.query.dept.DeptUserQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.UserListRepo;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * <p>
 * Implementation of user query operations.
 * </p>
 * <p>
 * Manages user retrieval, validation, and association management. Provides comprehensive user
 * querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports user detail retrieval, paginated listing, validation, quota management, and association
 * handling for comprehensive user administration.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
@SummaryQueryRegister(name = "User", table = "user0", topAuthority = TOP_TENANT_ADMIN, ignoreDeleted = true,
    groupByColumns = {"created_date", "enabled", "locked", "gender", "sys_admin"})
public class UserQueryImpl implements UserQuery {

  @Resource
  private UserRepo userRepo;
  @Resource
  private UserListRepo userListRepo;
  @Resource
  private UserSearchRepo userSearchRepo;
  @Resource
  private AuthUserRepo commonAuthUserRepo;
  @Resource
  private DeptUserQuery userDeptQuery;
  @Resource
  private GroupUserQuery userGroupQuery;
  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;
  @Resource
  private TenantQuery tenantQuery;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves detailed user information by ID.
   * </p>
   * <p>
   * Fetches complete user record with optional association joining. Enriches user data with OAuth
   * information and associations.
   * </p>
   */
  @SneakyThrow0
  @Override
  public User detail(Long id, boolean joinAssoc) {
    return new BizTemplate<User>(true, true) {
      User userDb;

      @Override
      protected void checkParams() {
        userDb = checkAndFind(id);
      }

      @Override
      protected User process() {
        AuthUser authUser = commonAuthUserRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "OUser"));
        UserConverter.assembleOauthUserInfo(userDb, authUser);
        if (joinAssoc) {
          userDb.setTags(orgTagTargetQuery.findAllByTarget(OrgTargetType.USER, id));
          userDb.setDepts(userDeptQuery.findAllByUserId(id));
          userDb.setGroups(userGroupQuery.findAllByUserId(id));
        }
        return userDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves users with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated user results
   * with comprehensive data.
   * </p>
   */
  @SneakyThrow0
  @Override
  public Page<User> list(GenericSpecification<User> spec, Pageable pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<User>>(true, true) {
      @Override
      protected Page<User> process() {
        return fullTextSearch
            ? userSearchRepo.find(spec.getCriteria(), pageable, User.class, match)
            : userListRepo.find(spec.getCriteria(), pageable, User.class, null);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates username and returns user ID if exists.
   * </p>
   * <p>
   * Checks username existence and logs duplicate warnings. Returns user ID if username exists, null
   * otherwise.
   * </p>
   */
  @Override
  public Long checkUsername(String username) {
    return new BizTemplate<Long>(false) {

      @Override
      protected Long process() {
        List<User> usersDb = userRepo.findAllByUsername(username);
        if (isNotEmpty(usersDb) && usersDb.size() > 1) {
          log.error("The username `{}` is a duplicate", username);
        }
        return isEmpty(usersDb) ? null : usersDb.get(0).getId();
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves system administrators for current tenant.
   * </p>
   * <p>
   * Returns all system admin users with first admin flag. Sets first system admin indicator for
   * priority handling.
   * </p>
   */
  @Override
  public List<User> getSysAdmins() {
    return new BizTemplate<List<User>>() {

      @Override
      protected List<User> process() {
        List<User> users = userRepo.findAllSysAdminUser(getOptTenantId());
        if (isNotEmpty(users)) {
          for (int i = 0; i < users.size(); i++) {
            users.get(i).setFirstSysAdmin(i == 0);
          }
        }
        return users;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves user by ID.
   * </p>
   * <p>
   * Returns user by user ID without additional validation. Returns null if user does not exist.
   * </p>
   */
  @Override
  public User findById(Long id) {
    return userRepo.findByUserId(id);
  }

  /**
   * <p>
   * Retrieves users by IDs.
   * </p>
   * <p>
   * Returns users for the specified user IDs. Returns empty list if no users found.
   * </p>
   */
  @Override
  public List<User> findByIdIn(Collection<Long> ids) {
    return userRepo.findByIdIn(ids);
  }

  /**
   * <p>
   * Finds users with expired lock.
   * </p>
   * <p>
   * Returns user IDs that have exceeded lock period. Used for automatic unlock processing.
   * </p>
   */
  @Override
  public Set<Long> findLockExpire(Long count) {
    return userRepo.findLockExpire(LocalDateTime.now(), count);
  }

  /**
   * <p>
   * Finds users with expired unlock.
   * </p>
   * <p>
   * Returns user IDs that have exceeded unlock period. Used for automatic lock processing.
   * </p>
   */
  @Override
  public Set<Long> findUnlockExpire(Long count) {
    return userRepo.findUnockExpire(LocalDateTime.now(), count);
  }

  /**
   * <p>
   * Retrieves tenant detail by ID.
   * </p>
   * <p>
   * Delegates to tenant query for tenant information. Returns complete tenant details.
   * </p>
   */
  @Override
  public Tenant tenantDetail(Long id) {
    return tenantQuery.detail(id);
  }

  /**
   * <p>
   * Finds signup or first system admin user for tenant.
   * </p>
   * <p>
   * Returns main system admin user for the specified tenant. Throws ResourceNotFound if no admin
   * user exists.
   * </p>
   */
  @Override
  public User findSignupOrFirstSysAdminUser(Long tenantId) {
    return userRepo.findMainSysAdminUser(tenantId)
        .orElseThrow(() -> ResourceNotFound.of(tenantId, "SignupOrFirstSysAdminUser"));
  }

  /**
   * <p>
   * Counts valid system admin users for current tenant.
   * </p>
   * <p>
   * Returns count of enabled system admin users. Used for quota and validation purposes.
   * </p>
   */
  @Override
  public int countValidSysAdminUser() {
    return userRepo.countValidSysAdminUser(getOptTenantId());
  }

  /**
   * <p>
   * Validates username uniqueness for update.
   * </p>
   * <p>
   * Ensures username is unique when updating user. Excludes current user from uniqueness check.
   * </p>
   */
  @Override
  public void checkUsernameUpdate(String username, Long id) {
    List<User> userDb = userRepo.findByUsernameAndIdNot(username, id);
    ProtocolAssert.assertResourceExisted(userDb, RESOURCE_ALREADY_EXISTS_T,
        new Object[]{username, "Username"});
  }

  /**
   * <p>
   * Validates mobile uniqueness for update.
   * </p>
   * <p>
   * Ensures mobile is unique when updating user. Excludes current user from uniqueness check.
   * </p>
   */
  @Override
  public void checkUpdateMobile(String mobile, Long id) {
    List<User> userDb = userRepo.findByMobileAndIdNot(mobile, id);
    ProtocolAssert.assertResourceExisted(userDb, RESOURCE_ALREADY_EXISTS_T,
        new Object[]{mobile, "Mobile"});
  }

  /**
   * <p>
   * Validates email uniqueness for update.
   * </p>
   * <p>
   * Ensures email is unique when updating user. Excludes current user from uniqueness check.
   * </p>
   */
  @Override
  public void checkUpdateEmail(String email, Long id) {
    List<User> userDb = userRepo.findByEmailAndIdNot(email, id);
    ProtocolAssert.assertResourceExisted(userDb, RESOURCE_ALREADY_EXISTS_T,
        new Object[]{email, "Email"});
  }

  /**
   * <p>
   * Validates and retrieves user by ID.
   * </p>
   * <p>
   * Returns user with existence validation. Throws ResourceNotFound if user does not exist.
   * </p>
   */
  @Override
  public User checkAndFind(Long id) {
    return userRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "User"));
  }

  /**
   * <p>
   * Validates and retrieves users by IDs.
   * </p>
   * <p>
   * Returns users with existence validation. Validates that all requested user IDs exist.
   * </p>
   */
  @Override
  public List<User> checkAndFind(Collection<Long> ids) {
    List<User> users = userRepo.findAllById(ids);
    ProtocolAssert.assertResourceNotFound(users, ids.iterator().next(), "User");

    if (ids.size() != users.size()) {
      for (User user : users) {
        ProtocolAssert.assertResourceNotFound(ids.contains(user.getId()), user.getId(), "User");
      }
    }
    return users;
  }

  /**
   * <p>
   * Validates user addition quota for tenant.
   * </p>
   * <p>
   * Checks user quota and related association quotas. Validates department, group, and tag quotas
   * for user addition.
   * </p>
   */
  @Override
  public void checkAddQuota(Tenant optTenant, User user, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags) {
    Long optTenantId = optTenant.getId();
    // Check user quota
    checkUserQuota(optTenantId, 1, user.getId());

    // Check user and dept quota
    userDeptQuery.checkUserDeptReplaceQuota(optTenantId, isEmpty(deptUsers) ? 0 : deptUsers.size(),
        user.getId());
    if (isNotEmpty(deptUsers)) {
      for (DeptUser userDept : deptUsers) {
        // Noteworthy: Duplicate data will trigger the database uniqueness constraint
        userDeptQuery.checkDeptUserAppendQuota(optTenantId, 1, userDept.getDeptId());
      }
    }

    // Check user and group quota
    userGroupQuery.checkUserGroupReplaceQuota(optTenantId, isEmpty(groupUsers) ? 0
        : groupUsers.size(), user.getId());
    if (isNotEmpty(groupUsers)) {
      for (GroupUser groupUser : groupUsers) {
        // Noteworthy: Duplicate data will trigger the database uniqueness constraint
        userGroupQuery.checkGroupUserAppendQuota(optTenantId, 1, groupUser.getGroupId());
      }
    }

    // Check user and tag quota
    orgTagTargetQuery.checkTargetTagQuota(optTenantId, isEmpty(userTags) ? 0
        : userTags.size(), user.getId());
  }

  /**
   * <p>
   * Validates admin operation permissions.
   * </p>
   * <p>
   * Only allows admin to operate admin users for delete, enable/disable, lock/unlock. Bypasses
   * validation for internal API, TO user, or tenant system admin.
   * </p>
   */
  @Override
  public void checkRefuseOperateAdmin(List<User> users) {
    if (isEmpty(users) || PrincipalContextUtils.isInnerApi() || isToUser() || isTenantSysAdmin()) {
      return;
    }
    for (User u : users) {
      ProtocolAssert.assertForbidden(!u.getSysAdmin(), USER_REFUSE_OPERATE_ADMIN,
          FORBIDDEN_KEY);
    }
  }

  /**
   * <p>
   * Validates signup account uniqueness.
   * </p>
   * <p>
   * Checks email and mobile uniqueness for signup accounts. Temporarily disables multi-tenant
   * control for global uniqueness check.
   * </p>
   */
  @Override
  public void checkSignupAccounts(User user) {
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(false);
    }
    if (user.getSignupAccountType().isEmail()) {
      if (userRepo.countBySignupAccountAndSignupAccountType(user.getSignupAccount(),
          SignupType.EMAIL) > 0) {
        throw ProtocolException.of(USER_ACCOUNT_EXISTED_ERROR,
            new Object[]{user.getSignupAccount()});
      }
    }
    if (user.getSignupAccountType().isMobile()) {
      if (userRepo.countBySignupAccountAndSignupAccountType(user.getSignupAccount(),
          SignupType.MOBILE) > 0) {
        throw ProtocolException.of(USER_ACCOUNT_EXISTED_ERROR,
            new Object[]{user.getSignupAccount()});
      }
    }
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(true);
    }
  }

  /**
   * <p>
   * Validates username uniqueness for addition.
   * </p>
   * <p>
   * Checks username uniqueness across all tenants. Temporarily disables multi-tenant control for
   * global check.
   * </p>
   */
  @Override
  public void checkAddUsername(String username) {
    if (isEmpty(username)) {
      return;
    }
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(false);
    }
    List<User> existedUsernameUsers = userRepo.findAllByUsername(username);
    if (isNotEmpty(existedUsernameUsers)) {
      throw ResourceExisted.of(existedUsernameUsers.get(0).getUsername(), "User");
    }
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(true);
    }
  }

  /**
   * <p>
   * Validates mobile uniqueness for addition.
   * </p>
   * <p>
   * Checks mobile uniqueness based on user source type. Handles different validation rules for
   * signup vs add user scenarios.
   * </p>
   */
  @Override
  public void checkAddMobile(String mobile, UserSource userSource) {
    if (isEmpty(mobile)) {
      return;
    }
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(false);
    }
    List<User> existedMobileUsers = userRepo.findAllByMobileIn(Collections.singleton(mobile));
    if (isEmpty(existedMobileUsers)) {
      return;
    }
    if (UserSource.isNewSignup(userSource)) {
      existedMobileUsers.forEach(existed -> {
        if (UserSource.isNewSignup(existed.getSource())) {
          throw ResourceExisted.of(RESOURCE_ALREADY_EXISTS_T,
              new Object[]{existed.getUsername(), "Mobile"});
        }
      });
    } else if (UserSource.isAddUser(userSource)) {
      existedMobileUsers.forEach(existed -> {
        if (existed.getTenantId().equals(getOptTenantId()) && existed.getMobile().equals(mobile)) {
          throw ResourceExisted.of(RESOURCE_ALREADY_EXISTS_T,
              new Object[]{existed.getMobile(), "Mobile"});
        }
      });
    } else {
      throw ResourceExisted.of(RESOURCE_ALREADY_EXISTS_T,
          new Object[]{existedMobileUsers.get(0).getMobile(), "Mobile"});
    }
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(true);
    }
  }

  /**
   * <p>
   * Validates email uniqueness for addition.
   * </p>
   * <p>
   * Checks email uniqueness based on user source type. Handles different validation rules for
   * signup vs add user scenarios.
   * </p>
   */
  @Override
  public void checkAddEmail(String email, UserSource userSource) {
    if (isEmpty(email)) {
      return;
    }
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(false);
    }
    List<User> existedEmailUsers = userRepo.findAllByEmailIn(Collections.singleton(email));
    if (isEmpty(existedEmailUsers)) {
      return;
    }
    if (UserSource.isNewSignup(userSource)) {
      existedEmailUsers.forEach(existed -> {
        if (UserSource.isNewSignup(existed.getSource())) {
          throw ResourceExisted.of(RESOURCE_ALREADY_EXISTS_T,
              new Object[]{existed.getUsername(), "Email"});
        }
      });
    } else if (UserSource.isAddUser(userSource)) {
      existedEmailUsers.forEach(existed -> {
        if (existed.getTenantId().equals(getOptTenantId()) && existed.getEmail().equals(email)) {
          throw ResourceExisted.of(RESOURCE_ALREADY_EXISTS_T,
              new Object[]{existed.getEmail(), "Email"});
        }
      });
    } else {
      throw ResourceExisted.of(RESOURCE_ALREADY_EXISTS_T,
          new Object[]{existedEmailUsers.get(0).getEmail(), "Email"});
    }
    if (isMultiTenantCtrl) {
      setMultiTenantCtrl(true);
    }
  }

  /**
   * <p>
   * Validates user quota for tenant.
   * </p>
   * <p>
   * Checks if adding users would exceed tenant quota limits. Throws appropriate exception if quota
   * would be exceeded.
   * </p>
   */
  @Override
  public void checkUserQuota(Long tenantId, long incr, Long userId) {
    if (incr > 0) {
      long num = userRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.User,
          Collections.singleton(userId), num + incr);
    }
  }

}
