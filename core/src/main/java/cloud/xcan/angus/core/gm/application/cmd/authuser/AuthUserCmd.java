package cloud.xcan.angus.core.gm.application.cmd.authuser;


import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import java.util.Set;


public interface AuthUserCmd {

  void replace0(AuthUser user, Boolean initTenant);

  void passwordUpdate(Long id, String newPassword);

  void delete(Set<Long> ids);

  void realName(Long tenantId, TenantRealNameStatus realNameStatus);

}
