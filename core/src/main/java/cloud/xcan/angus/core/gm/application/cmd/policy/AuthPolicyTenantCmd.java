package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import java.util.List;


public interface AuthPolicyTenantCmd {

  void defaultPolicySet(Long appId, Long policyId);

  void defaultPolicyDelete(Long appId);

  void defaultPolicyDelete0(Long tenantId, Long appId);

  void intAndOpenAppByTenantWhenSignup(Long tenantId);

  void intAppAndPolicyByTenantAndApp(Long tenantId, App appDb);

  void appOpenPolicyCancel(Long tenantId, Long appId);

  void add0(List<AuthPolicyOrg> authTenantPolicies);

}
