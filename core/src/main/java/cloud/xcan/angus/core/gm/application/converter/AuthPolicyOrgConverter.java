package cloud.xcan.angus.core.gm.application.converter;


import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.convert;
import static cloud.xcan.angus.spec.utils.ObjectUtils.longSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuthPolicyOrgConverter {

  public static List<AuthPolicyOrg> addToPolicyUser(Long appId, List<AuthPolicy> policiesDb,
      Set<Long> userIds) {
    List<AuthPolicyOrg> allAuths = new ArrayList<>();
    for (AuthPolicy policy : policiesDb) {
      for (Long userId : userIds) {
        allAuths.add(new AuthPolicyOrg().setPolicyId(policy.getId())
            .setOrgType(AuthOrgType.USER).setOrgId(userId)
            .setAppId(appId)
            .setPolicyType(policy.getType())
            .setGrantScope(PolicyGrantScope.TENANT_ORG)
            .setOpenAuth(false)
            .setTenantId(getOptTenantId())
            .setCreatedBy(longSafe(getUserId(), -1L))
            .setDefault0(false)
            .setCreatedDate(LocalDateTime.now()));
      }
    }
    return allAuths;
  }

  public static List<AuthPolicyOrg> addToPolicyDept(Long appId, List<AuthPolicy> policiesDb,
      Set<Long> deptIds) {
    List<AuthPolicyOrg> allAuths = new ArrayList<>();
    for (AuthPolicy policy : policiesDb) {
      for (Long deptId : deptIds) {
        allAuths.add(new AuthPolicyOrg().setPolicyId(policy.getId())
            .setOrgType(AuthOrgType.DEPT).setOrgId(deptId)
            .setAppId(appId)
            .setPolicyType(policy.getType())
            .setGrantScope(PolicyGrantScope.TENANT_ORG)
            .setOpenAuth(false)
            .setTenantId(getOptTenantId())
            .setCreatedBy(longSafe(getUserId(), -1L))
            .setDefault0(false)
            .setCreatedDate(LocalDateTime.now()));
      }
    }
    return allAuths;
  }

  public static List<AuthPolicyOrg> addToPolicyGroup(Long appId, List<AuthPolicy> policiesDb,
      Set<Long> groupIds) {
    List<AuthPolicyOrg> allAuths = new ArrayList<>();
    for (AuthPolicy policy : policiesDb) {
      for (Long groupId : groupIds) {
        allAuths.add(new AuthPolicyOrg().setPolicyId(policy.getId())
            .setOrgType(AuthOrgType.GROUP).setOrgId(groupId)
            .setAppId(appId)
            .setPolicyType(policy.getType())
            .setGrantScope(PolicyGrantScope.TENANT_ORG)
            .setOpenAuth(false)
            .setTenantId(getOptTenantId())
            .setCreatedBy(longSafe(getUserId(), -1L))
            .setDefault0(false)
            .setCreatedDate(LocalDateTime.now()));
      }
    }
    return allAuths;
  }

  public static AuthPolicy objectArrToOrgAuthPolicyAndOrg(Object[] objects) {
    AuthPolicy policy = objectArrToOrgAuthPolicy(objects);

    policy.setAuthBy(convert(objects[15], Long.class))
        .setAuthDate(convert(objects[16], LocalDateTime.class));
    return policy;
  }

  public static AuthPolicy objectArrToUserAssociatedAuthPolicyAndOrg(Object[] objects) {
    AuthPolicy policy = objectArrToOrgAuthPolicy(objects);

    policy.setOrgType(convert(objects[15], AuthOrgType.class))
        .setOrgId(convert(objects[16], Long.class))
        .setGrantScope(convert(objects[17], PolicyGrantScope.class))
        .setCurrentDefault0(convert(objects[18], Boolean.class))
        .setOpenAuth(convert(objects[19], Boolean.class))
        .setAuthBy(convert(objects[20], Long.class))
        .setAuthDate(convert(objects[21], LocalDateTime.class));
    return policy;
  }

  public static AuthPolicy objectArrToUserAssociatedAuthPolicy(Object[] objects) {
    return objectArrToOrgAuthPolicy(objects);
  }

  public static AuthPolicy objectArrToOrgAuthPolicy(Object[] objects) {
    AuthPolicy policy = new AuthPolicy().setId(convert(objects[0], Long.class))
        .setName(convert(objects[1], String.class))
        .setCode(convert(objects[2], String.class))
        .setEnabled(convert(objects[3], Boolean.class))
        .setType(convert(objects[4], PolicyType.class))
        .setDefault0(convert(objects[5], Boolean.class))
        .setGrantStage(convert(objects[6], PolicyGrantStage.class))
        .setDescription(convert(objects[7], String.class))
        .setAppId(convert(objects[8], Long.class))
        .setClientId(convert(objects[9], String.class));

    policy.setTenantId(convert(objects[10], Long.class))
        .setCreatedBy(convert(objects[11], Long.class))
        .setCreatedDate(convert(objects[12], LocalDateTime.class))
        .setLastModifiedBy(convert(objects[13], Long.class))
        .setLastModifiedDate(convert(objects[14], LocalDateTime.class));
    return policy;
  }
}
