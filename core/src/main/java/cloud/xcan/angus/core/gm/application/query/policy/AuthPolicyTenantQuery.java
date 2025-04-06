package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import java.util.List;

public interface AuthPolicyTenantQuery {

  List<AuthPolicyOrg> defaultPolicy();

  List<App> tenantAppList();

  List<AppFunc> tenantAppFuncList(Long appId);

  AuthPolicyOrg findAdminOpenAuthByAppIdAndTenantId(Long appId, Long tenantId);
}
