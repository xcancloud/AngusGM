package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQuery {

  User detail(Long id, boolean joinAssoc);

  Page<User> find(GenericSpecification<User> spec, Pageable pageable);

  Tenant tenantDetail(Long id);

  User findSignupOrFirstSysAdminUser(Long tenantId);

  List<User> getSysAdmins();

  Set<Long> findLockExpire(Long count);

  Set<Long> findUnlockExpire(Long count);

  User checkAndFind(Long id);

  int countValidSysAdminUser();

  void checkUsernameUpdate(String username, Long id);

  void checkUpdateMobile(String mobile, Long id);

  void checkUpdateEmail(String email, Long id);

  Long checkUsername(String username);

  List<User> checkAndFind(Collection<Long> ids);

  void checkAddQuota(Tenant optTenant, User user, List<DeptUser> deptUsers,
      List<GroupUser> groupUsers, List<OrgTagTarget> userTags);

  /**
   * Only allow admin to operate admin: delete, enabled or disabled, locked or unlocked
   */
  void checkRefuseOperateAdmin(List<User> users);

  void checkSignupAccounts(User user);

  void checkAddUsername(String username);

  void checkAddMobile(String mobile, UserSource userSource);

  void checkAddEmail(String email, UserSource userSource);

  void checkUserQuota(Long tenantId, long incr, Long userId);

}
