package cloud.xcan.angus.core.gm.interfaces.to.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleSearchDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleUserVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;


public class TORoleAssembler {

  public static TORole dtoToRole(TORoleAddDto dto) {
    return new TORole()
        .setAppId(dto.getAppId())
        .setEnabled(true)
        .setName(dto.getName())
        .setCode(dto.getCode())
        .setDescription(dto.getDescription());
  }

  public static TORole updateToRole(TORoleUpdateDto dto) {
    return new TORole()
        .setId(dto.getId())
        .setName(dto.getName())
        .setDescription(dto.getDescription());
  }

  public static TORole replaceToRole(TORoleReplaceDto dto) {
    return new TORole()
        .setId(dto.getId())
        .setEnabled(isNull(dto.getId()) ? true : null)
        .setName(dto.getName())
        .setCode(isNull(dto.getId()) ? dto.getCode() : null)
        .setDescription(dto.getDescription());
  }

  public static TORole enabledToRole(EnabledOrDisabledDto dto) {
    return new TORole().setId(dto.getId())
        .setEnabled(dto.getEnabled());
  }

  public static TORoleDetailVo toTORoleDetailVo(TORole role) {
    return new TORoleDetailVo().setId(role.getId())
        .setCode(role.getCode())
        .setName(role.getName())
        .setDescription(role.getDescription())
        .setAppId(role.getAppId())
        .setEnabled(role.getEnabled())
        .setCreatedDate(role.getCreatedDate())
        .setCreatedBy(role.getCreatedBy())
        .setToUsers(toPolicyUserVos(role.getUsers()));
  }

  public static TORoleVo toRoleVo(TORole role) {
    return new TORoleVo().setAppId(role.getAppId())
        .setCode(role.getCode())
        .setName(role.getName())
        .setId(role.getId())
        .setDescription(role.getDescription())
        .setEnabled(role.getEnabled())
        .setCreatedBy(role.getCreatedBy())
        .setCreatedDate(role.getCreatedDate());
  }

  private static List<TORoleUserVo> toPolicyUserVos(List<User> users) {
    return isEmpty(users) ? null : users.stream()
        .map(user -> new TORoleUserVo()
            .setId(user.getId())
            .setUsername(user.getUsername())
            .setFullName(user.getFullName())
            .setAvatar(user.getAvatar())
            .setEmail(user.getEmail())
            .setMobile(user.getMobile())
        ).collect(Collectors.toList());
  }

  public static Specification<TORole> getSpecification(TORoleFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name", "code")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(TORoleSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id")
        .build();
  }

}
