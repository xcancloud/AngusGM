package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagSearchDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;


public class OrgTagAssembler {

  public static OrgTag addDtoToDomain(OrgTagAddDto dto) {
    return new OrgTag().setName(dto.getName());
  }

  public static OrgTag updateDtoToDomain(OrgTagUpdateDto dto) {
    return new OrgTag().setId(dto.getId()).setName(dto.getName());
  }

  public static OrgTagDetailVo domainToDetailVo(OrgTag tag) {
    return new OrgTagDetailVo().setId(tag.getId()).setName(tag.getName())
        .setTenantId(tag.getTenantId())
        .setCreatedBy(tag.getCreatedBy())
        .setCreatedDate(tag.getCreatedDate())
        .setLastModifiedBy(tag.getLastModifiedBy())
        .setLastModifiedDate(tag.getLastModifiedDate());
  }

  public static OrgTagTargetDetailVo tagTargetToVo(OrgTagTarget tagTarget) {
    return new OrgTagTargetDetailVo().setId(tagTarget.getId())
        .setTagId(tagTarget.getTagId())
        .setTagName(tagTarget.getTagName())
        .setTargetId(tagTarget.getTargetId())
        .setTargetName(tagTarget.getTargetName())
        .setTargetType(tagTarget.getTargetType())
        .setCreatedBy(tagTarget.getCreatedBy())
        .setCreatedDate(tagTarget.getCreatedDate())
        .setTargetCreatedBy(tagTarget.getTargetCreatedBy())
        .setTargetCreatedDate(tagTarget.getTargetCreatedDate());
  }

  public static OrgTagTarget addDtoToTagTarget(Long tagId, OrgTagTargetAddDto dto) {
    return new OrgTagTarget().setTargetId(dto.getTargetId())
        .setTargetType(dto.getTargetType())
        .setTagId(tagId);
  }

  public static Specification<OrgTag> getSpecification(OrgTagFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate")
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(OrgTagSearchDto dto) {
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate")
        .matchSearchFields("name")
        .build();
  }

}
