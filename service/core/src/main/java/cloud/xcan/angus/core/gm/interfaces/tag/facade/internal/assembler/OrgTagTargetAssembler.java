package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class OrgTagTargetAssembler {

  public static OrgTagTargetVo toTagTargetVo(OrgTagTarget tagTarget) {
    return new OrgTagTargetVo().setTagId(tagTarget.getTagId())
        .setTagName(tagTarget.getTagName())
        .setTargetId(tagTarget.getTargetId())
        .setTargetType(tagTarget.getTargetType())
        .setTargetName(tagTarget.getTargetName())
        .setCreatedBy(tagTarget.getCreatedBy())
        .setCreatedDate(tagTarget.getCreatedDate())
        .setTargetCreatedBy(tagTarget.getTargetCreatedBy())
        .setTargetCreatedDate(tagTarget.getTargetCreatedDate());
  }

  public static GenericSpecification<OrgTagTarget> getTagTargetSpecification(
      OrgTagTargetFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("targetId", "createdDate")
        .orderByFields("targetId", "createdDate")
        .matchSearchFields("targetName")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<OrgTagTarget> getTargetTagSpecification(
      OrgTargetTagFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("tagId", "createdDate")
        .orderByFields("tagId", "createdDate")
        .matchSearchFields("tagName")
        .build();
    return new GenericSpecification<>(filters);
  }

}
