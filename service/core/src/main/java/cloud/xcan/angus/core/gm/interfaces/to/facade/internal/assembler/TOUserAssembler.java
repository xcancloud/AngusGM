package cloud.xcan.angus.core.gm.interfaces.to.facade.internal.assembler;


import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserRoleVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class TOUserAssembler {

  public static TOUser addDtoToDomain(TOUserAddDto dto) {
    return new TOUser().setUserId(dto.getId());
  }

  public static TOUserDetailVo toDetailVo(TOUser user) {
    return new TOUserDetailVo()
        .setId(user.getId())
        .setUserId(user.getUserId())
        .setCreatedBy(user.getCreatedBy())
        .setCreatedDate(user.getCreatedDate())
        .setToRoles(toPolicyVos(user.getToRoles()));
  }

  public static TOUserVo toVo(TOUser user) {
    return new TOUserVo()
        .setId(user.getId())
        .setUserId(user.getUserId())
        .setCreatedBy(user.getCreatedBy())
        .setCreatedDate(user.getCreatedDate());
  }

  public static List<TOUserRoleVo> toPolicyVos(List<TORole> roles) {
    return isEmpty(roles) ? null : roles.stream().map(
            policy -> new TOUserRoleVo()
                .setCode(policy.getCode())
                .setName(policy.getName())
                .setId(policy.getId())
                .setDescription(policy.getDescription())
                .setCreatedDate(policy.getCreatedDate()))
        .collect(Collectors.toList());
  }

  public static GenericSpecification<TOUser> getSpecification(TOUserFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "userId", "createdDate")
        .orderByFields("id", "userId", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
