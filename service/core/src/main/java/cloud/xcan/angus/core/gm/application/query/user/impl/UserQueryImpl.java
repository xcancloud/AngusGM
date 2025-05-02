package cloud.xcan.angus.core.gm.application.query.user.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.USER_ACCOUNT_EXISTED_ERROR;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.USER_REFUSE_OPERATE_ADMIN;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isInnerApi;
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
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
@Slf4j
@SummaryQueryRegister(name = "User", table = "user0", topAuthority = TOP_TENANT_ADMIN, ignoreDeleted = true,
    groupByColumns = {"created_date", "source", "sys_admin", "enabled",
        "locked", "gender"})
public class UserQueryImpl implements UserQuery {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserListRepo userListRepo;

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
        userDb.setTags(orgTagTargetQuery.findAllByTarget(OrgTargetType.USER, id));
        if (joinAssoc) {
          userDb.setDepts(userDeptQuery.findAllByUserId(id));
          userDb.setGroups(userGroupQuery.findAllByUserId(id));
        }
        return userDb;
      }
    }.execute();
  }

  @SneakyThrow0
  @Override
  public Page<User> find(GenericSpecification<User> spec, Pageable pageable) {
    return new BizTemplate<Page<User>>(true, true) {

      @Override
      protected Page<User> process() {
        return userListRepo.find(spec.getCriteria(), pageable, User.class, null);
      }
    }.execute();
  }

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

  @Override
  public User findById(Long id) {
    return userRepo.findByUserId(id);
  }

  @Override
  public List<User> findByIdIn(Collection<Long> ids) {
    return userRepo.findByIdIn(ids);
  }

  @Override
  public Set<Long> findLockExpire(Long count) {
    return userRepo.findLockExpire(LocalDateTime.now(), count);
  }

  @Override
  public Set<Long> findUnlockExpire(Long count) {
    return userRepo.findUnockExpire(LocalDateTime.now(), count);
  }

  @Override
  public Tenant tenantDetail(Long id) {
    return tenantQuery.detail(id);
  }

  @Override
  public User findSignupOrFirstSysAdminUser(Long tenantId) {
    return userRepo.findMainSysAdminUser(tenantId)
        .orElseThrow(() -> ResourceNotFound.of(tenantId, "SignupOrFirstSysAdminUser"));
  }

  @Override
  public int countValidSysAdminUser() {
    return userRepo.countValidSysAdminUser(getOptTenantId());
  }

  @Override
  public void checkUsernameUpdate(String username, Long id) {
    List<User> userDb = userRepo.findByUsernameAndIdNot(username, id);
    ProtocolAssert.assertResourceExisted(userDb, RESOURCE_ALREADY_EXISTS_T,
        new Object[]{username, "Username"});
  }

  @Override
  public void checkUpdateMobile(String mobile, Long id) {
    List<User> userDb = userRepo.findByMobileAndIdNot(mobile, id);
    ProtocolAssert.assertResourceExisted(userDb, RESOURCE_ALREADY_EXISTS_T,
        new Object[]{mobile, "Mobile"});
  }

  @Override
  public void checkUpdateEmail(String email, Long id) {
    List<User> userDb = userRepo.findByEmailAndIdNot(email, id);
    ProtocolAssert.assertResourceExisted(userDb, RESOURCE_ALREADY_EXISTS_T,
        new Object[]{email, "Email"});
  }

  @Override
  public User checkAndFind(Long id) {
    return userRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "User"));
  }

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
        // Noteworthy:: Duplicate data will trigger the database uniqueness constraint
        userDeptQuery.checkDeptUserAppendQuota(optTenantId, 1, userDept.getDeptId());
      }
    }

    // Check user and group quota
    userGroupQuery.checkUserGroupReplaceQuota(optTenantId, isEmpty(groupUsers) ? 0
        : groupUsers.size(), user.getId());
    if (isNotEmpty(groupUsers)) {
      for (GroupUser groupUser : groupUsers) {
        // Noteworthy:: Duplicate data will trigger the database uniqueness constraint
        userGroupQuery.checkGroupUserAppendQuota(optTenantId, 1, groupUser.getGroupId());
      }
    }

    // Check user and tag quota
    orgTagTargetQuery.checkTargetTagQuota(optTenantId, isEmpty(userTags) ? 0
        : userTags.size(), user.getId());
  }

  /**
   * Only allow admin to operate admin: delete, enabled or disabled, locked or unlocked
   *
   * @param users operate users
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

  @Override
  public void checkUserQuota(Long tenantId, long incr, Long userId) {
    if (incr > 0) {
      long num = userRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.User,
          Collections.singleton(userId), num + incr);
    }
  }

}
