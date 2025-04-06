package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.longSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyGroupFindDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AuthPolicyGroupAssembler {

  public static List<AuthPolicyOrg> addToPolicyGroup(Long policyId, Set<Long> groupIds) {
    return groupIds.stream().map(groupId -> new AuthPolicyOrg().setPolicyId(policyId)
        .setOrgType(AuthOrgType.GROUP).setOrgId(groupId)
        /*.setAppId()*/
        .setGrantScope(PolicyGrantScope.TENANT_ORG)
        .setOpenAuth(false)
        .setTenantId(getOptTenantId())
        .setCreatedBy(longSafe(getUserId(), -1L))
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now())
    ).collect(Collectors.toList());
  }

  public static List<AuthPolicyOrg> addToGroupPolicy(Long groupId, Set<Long> policyIds) {
    return policyIds.stream().map(policyId -> new AuthPolicyOrg().setPolicyId(policyId)
        .setOrgType(AuthOrgType.GROUP).setOrgId(groupId)
        /*.setAppId()*/
        .setGrantScope(PolicyGrantScope.TENANT_ORG)
        .setOpenAuth(false)
        .setTenantId(getOptTenantId())
        .setCreatedBy(longSafe(getUserId(), -1L))
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now())
    ).collect(Collectors.toList());
  }

  public static GroupListVo toGroupListVo(Group group) {
    return new GroupListVo()
        .setId(group.getId())
        .setUserNum(group.getUserNum())
        .setName(group.getName())
        .setCode(group.getCode())
        .setEnabled(group.getEnabled())
        .setRemark(group.getRemark())
        .setTenantId(group.getTenantId())
        .setCreatedBy(group.getCreatedBy())
        .setCreatedDate(group.getCreatedDate())
        .setLastModifiedBy(group.getLastModifiedBy())
        .setLastModifiedDate(group.getLastModifiedDate());
  }

  public static GenericSpecification<Group> getSpecification(AuthPolicyGroupFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id", "name", "createdDate")
        //.inAndNotFields("tagId")
        .build();
    return new GenericSpecification<>(filters);
  }

}
