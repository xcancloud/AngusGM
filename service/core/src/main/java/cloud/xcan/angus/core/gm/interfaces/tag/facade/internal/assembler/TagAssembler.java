package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagAllVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * Tag assembler for DTO/Domain/VO conversion
 */
public class TagAssembler {

  /**
   * Convert CreateDto to Domain
   */
  public static Tag toCreateDomain(TagCreateDto dto) {
    Tag tag = new Tag();
    tag.setName(dto.getName());
    tag.setDescription(dto.getDescription());
    return tag;
  }

  /**
   * Convert UpdateDto to Domain
   */
  public static Tag toUpdateDomain(Long id, TagUpdateDto dto) {
    Tag tag = new Tag();
    tag.setId(id);
    tag.setName(dto.getName());
    tag.setDescription(dto.getDescription());
    return tag;
  }

  /**
   * Convert Domain to DetailVo
   */
  public static TagDetailVo toDetailVo(Tag tag) {
    TagDetailVo vo = new TagDetailVo();
    vo.setId(tag.getId());
    vo.setName(tag.getName());
    vo.setDescription(tag.getDescription());
    vo.setIsSystem(nullSafe(tag.getIsSystem(), false));
    vo.setUsageCount(nullSafe(tag.getUsageCount(), 0L));
    // TODO: Set applications list if available

    // Set auditing fields
    vo.setTenantId(tag.getTenantId());
    vo.setCreatedBy(tag.getCreatedBy());
    vo.setCreator(tag.getCreatedByName());
    vo.setCreatedDate(tag.getCreatedDate());
    vo.setModifiedBy(tag.getModifiedBy());
    vo.setModifier(tag.getModifiedByName());
    vo.setModifiedDate(tag.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static TagListVo toListVo(Tag tag) {
    TagListVo vo = new TagListVo();
    vo.setId(tag.getId());
    vo.setName(tag.getName());
    vo.setDescription(tag.getDescription());
    vo.setIsSystem(nullSafe(tag.getIsSystem(), false));
    vo.setUsageCount(nullSafe(tag.getUsageCount(), 0L));

    // Set auditing fields
    vo.setTenantId(tag.getTenantId());
    vo.setCreatedBy(tag.getCreatedBy());
    vo.setCreator(tag.getCreatedByName());
    vo.setCreatedDate(tag.getCreatedDate());
    vo.setModifiedBy(tag.getModifiedBy());
    vo.setModifier(tag.getModifiedByName());
    vo.setModifiedDate(tag.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to AllVo (simplified)
   */
  public static TagAllVo toAllVo(Tag tag) {
    TagAllVo vo = new TagAllVo();
    vo.setId(tag.getId());
    vo.setName(tag.getName());
    vo.setDescription(tag.getDescription());
    vo.setIsSystem(nullSafe(tag.getIsSystem(), false));
    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  public static GenericSpecification<Tag> getSpecification(TagFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }
}
