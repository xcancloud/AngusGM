package cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * Group assembler for DTO/Domain/VO conversion
 */
public class GroupAssembler {

  /**
   * Convert CreateDto to Domain
   */
  public static Group toCreateDomain(GroupCreateDto dto) {
    Group group = new Group();
    group.setName(dto.getName());
    group.setCode(dto.getCode());
    group.setDescription(dto.getDescription());
    group.setType(dto.getType());
    group.setOwnerId(dto.getOwnerId());
    group.setMemberIds(dto.getMemberIds());
    group.setStatus(dto.getStatus());
    return group;
  }

  /**
   * Convert UpdateDto to Domain
   */
  public static Group toUpdateDomain(Long id, GroupUpdateDto dto) {
    Group group = new Group();
    group.setId(id);
    group.setName(dto.getName());
    group.setDescription(dto.getDescription());
    group.setType(dto.getType());
    group.setOwnerId(dto.getOwnerId());
    group.setMemberIds(dto.getMemberIds());
    group.setStatus(dto.getStatus());
    return group;
  }

  /**
   * Convert Domain to DetailVo
   */
  public static GroupDetailVo toDetailVo(Group group) {
    GroupDetailVo vo = new GroupDetailVo();
    vo.setId(group.getId());
    vo.setName(group.getName());
    vo.setCode(group.getCode());
    vo.setDescription(group.getDescription());
    vo.setType(group.getType());
    vo.setStatus(group.getStatus());
    vo.setOwnerId(group.getOwnerId());
    vo.setOwnerName(group.getOwnerName());
    vo.setMemberCount(nullSafe(group.getMemberCount(), 0L));
    vo.setMemberIds(group.getMemberIds());

    // Set auditing fields
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreator(group.getCreatedByName());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifier(group.getModifiedByName());
    vo.setModifiedDate(group.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static GroupListVo toListVo(Group group) {
    GroupListVo vo = new GroupListVo();
    vo.setId(group.getId());
    vo.setName(group.getName());
    vo.setCode(group.getCode());
    vo.setDescription(group.getDescription());
    vo.setType(group.getType());
    vo.setStatus(group.getStatus());
    vo.setOwnerId(group.getOwnerId());
    vo.setOwnerName(group.getOwnerName());
    vo.setMemberCount(nullSafe(group.getMemberCount(), 0L));
    vo.setMemberIds(group.getMemberIds());

    // Set auditing fields
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreator(group.getCreatedByName());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifier(group.getModifiedByName());
    vo.setModifiedDate(group.getModifiedDate());

    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  public static GenericSpecification<Group> getSpecification(GroupFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }
}
