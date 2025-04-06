package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.longSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.pidSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyDeptFindDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AuthPolicyDeptAssembler {

  public static List<AuthPolicyOrg> addToPolicyDept(Long policyId, Set<Long> deptIds) {
    return deptIds.stream().map(deptId -> new AuthPolicyOrg().setPolicyId(policyId)
        .setOrgType(AuthOrgType.DEPT).setOrgId(deptId)
        /*.setAppId()*/
        .setGrantScope(PolicyGrantScope.TENANT_ORG)
        .setOpenAuth(false)
        .setTenantId(getOptTenantId())
        .setCreatedBy(longSafe(getUserId(), -1L))
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now())
    ).collect(Collectors.toList());
  }

  public static List<AuthPolicyOrg> addToDeptPolicy(Long deptId, Set<Long> policyIds) {
    return policyIds.stream().map(policyId -> new AuthPolicyOrg().setPolicyId(policyId)
        .setOrgType(AuthOrgType.DEPT).setOrgId(deptId)
        /*.setAppId()*/
        .setGrantScope(PolicyGrantScope.TENANT_ORG)
        .setOpenAuth(false)
        .setTenantId(getOptTenantId())
        .setCreatedBy(longSafe(getUserId(), -1L))
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now())
    ).collect(Collectors.toList());
  }

  public static DeptListVo toDeptListVo(Dept dept) {
    return new DeptListVo().setId(dept.getId())
        .setCode(dept.getCode())
        .setName(dept.getName())
        .setPid(pidSafe(dept.getPid()))
        .setLevel(dept.getLevel())
        .setHasSubDept(dept.getHasSubDept())
        .setTenantId(dept.getTenantId())
        .setCreatedBy(dept.getCreatedBy())
        .setCreatedDate(dept.getCreatedDate())
        .setLastModifiedBy(dept.getLastModifiedBy())
        .setLastModifiedDate(dept.getLastModifiedDate());
  }

  public static GenericSpecification<Dept> getSpecification(AuthPolicyDeptFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id", "name", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
