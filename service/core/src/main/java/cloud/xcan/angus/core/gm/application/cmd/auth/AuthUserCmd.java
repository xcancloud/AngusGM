package cloud.xcan.angus.core.gm.application.cmd.auth;


import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.user.User;
import java.util.List;
import java.util.Set;


public interface AuthUserCmd {

  void replaceAuthUser(User userDb, String password, boolean initTenant);

  void replace0(AuthUser user, Boolean initTenant);

  void passwordUpdate(Long id, String newPassword);

  void delete(Set<Long> ids);

  void realName(Long tenantId, TenantRealNameStatus realNameStatus);

  void deleteAuthorization(List<String> principalNames);
}
