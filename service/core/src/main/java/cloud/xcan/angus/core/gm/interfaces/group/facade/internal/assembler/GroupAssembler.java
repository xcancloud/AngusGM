package cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupSource;
import cloud.xcan.angus.api.gm.group.dto.GroupFindDto;
import cloud.xcan.angus.api.gm.group.dto.GroupSearchDto;
import cloud.xcan.angus.api.gm.group.vo.GroupDetailVo;
import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.info.IdAndName;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupAssembler {

  public static Group addDtoToDomain(GroupAddDto dto) {
    return new Group().setName(dto.getName())
        .setCode(dto.getCode())
        .setRemark(dto.getRemark())
        .setEnabled(Boolean.TRUE)
        .setSource(GroupSource.BACKGROUND_ADDED)
        .setTagIds(dto.getTagIds());
  }

  public static Group updateDtoToDomain(GroupUpdateDto dto) {
    return new Group().setId(dto.getId())
        .setName(dto.getName())
        .setCode(dto.getCode())
        .setRemark(dto.getRemark())
        .setTagIds(dto.getTagIds());
  }

  public static Group replaceDtoToDomain(GroupReplaceDto dto) {
    return new Group().setId(dto.getId()).setName(dto.getName())
        .setCode(dto.getCode())
        .setRemark(dto.getRemark())
        .setTagIds(dto.getTagIds());
  }

  public static Group enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new Group().setId(dto.getId()).setEnabled(dto.getEnabled());
  }

  public static GroupDetailVo toDetailVo(Group group) {
    return new GroupDetailVo()
        .setId(group.getId())
        .setName(group.getName())
        .setCode(group.getCode())
        .setSource(group.getSource())
        .setUserNum(group.getUserNum())
        .setEnabled(group.getEnabled())
        .setCreatedDate(group.getCreatedDate())
        .setRemark(group.getRemark())
        .setDirectoryId(group.getDirectoryId())
        .setDirectoryGidNumber(group.getDirectoryGidNumber())
        .setTenantId(group.getTenantId())
        .setCreatedBy(group.getCreatedBy())
        .setCreatedDate(group.getCreatedDate())
        .setLastModifiedBy(group.getLastModifiedBy())
        .setLastModifiedDate(group.getLastModifiedDate())
        .setTags(isEmpty(group.getTags()) ? null : group.getTags().stream()
            .map(tag -> new IdAndName().setId(tag.getTag().getId()).setName(tag.getTag().getName()))
            .collect(Collectors.toList()));
  }

  public static GroupListVo toListVo(Group group) {
    return new GroupListVo()
        .setId(group.getId())
        .setName(group.getName())
        .setCode(group.getCode())
        .setSource(group.getSource())
        .setUserNum(group.getUserNum())
        .setEnabled(group.getEnabled())
        .setRemark(group.getRemark())
        .setTenantId(group.getTenantId())
        .setCreatedBy(group.getCreatedBy())
        .setCreatedDate(group.getCreatedDate())
        .setLastModifiedBy(group.getLastModifiedBy())
        .setLastModifiedDate(group.getLastModifiedDate());
  }

  public static GenericSpecification<Group> getSpecification(GroupFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id", "name", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(GroupSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id", "name", "createdDate")
        .build();
  }
}
