package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.app.AppPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.app.AppPolicyVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class AuthPolicyAppAssembler {

  public static AppPolicyVo toPolicyListVo(AuthPolicy policy) {
    return new AppPolicyVo().setId(policy.getId())
        .setName(policy.getName())
        .setCode(policy.getCode())
        .setType(policy.getType())
        .setDefault0(policy.getDefault0())
        .setGrantStage(policy.getGrantStage())
        .setEnabled(policy.getEnabled())
        .setTenantId(policy.getTenantId())
        .setCreatedBy(policy.getCreatedBy())
        .setCreatedDate(policy.getCreatedDate());
  }

  public static GenericSpecification<AuthPolicy> getFindCriteria(AppPolicyFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("code", "name", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
