package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.longSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import java.time.LocalDateTime;


public class AuthTenantConverter {

  public static AuthPolicyOrg openGrantInitToPolicyTenant(Long tenantId, AuthPolicy policyDb) {
    return new AuthPolicyOrg()
        .setPolicyId(policyDb.getId())
        .setPolicyType(PolicyType.PRE_DEFINED)
        .setOrgType(AuthOrgType.TENANT)
        .setOrgId(tenantId)
        .setAppId(policyDb.getAppId())
        .setGrantScope(PolicyGrantScope.TENANT_SYS_ADMIN)
        .setOpenAuth(true)
        .setTenantId(tenantId)
        .setCreatedBy(longSafe(getUserId(), -1L)) // Job or door is possible
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now());
  }

  public static AuthPolicyOrg defaultInitToPolicyTenant(Long tenantId, AuthPolicy policyDb) {
    return new AuthPolicyOrg()
        .setPolicyId(policyDb.getId())
        .setPolicyType(policyDb.getType())
        .setOrgType(AuthOrgType.TENANT)
        .setOrgId(tenantId)
        .setAppId(policyDb.getAppId())
        .setGrantScope(PolicyGrantScope.TENANT_ALL_USER)
        .setOpenAuth(false)
        .setTenantId(tenantId)
        .setCreatedBy(longSafe(getUserId(), -1L)) // Job or door is possible
        .setDefault0(true)
        .setCreatedDate(LocalDateTime.now());
  }

}
