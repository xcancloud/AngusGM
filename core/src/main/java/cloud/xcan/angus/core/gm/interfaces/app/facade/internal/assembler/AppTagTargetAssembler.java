package cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagTargetVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class AppTagTargetAssembler {

  public static AppTagTargetVo toTagTargetVo(WebTagTarget tagTarget) {
    return new AppTagTargetVo().setTagId(tagTarget.getTagId())
        .setTagName(tagTarget.getTagName())
        .setTargetId(tagTarget.getTargetId())
        .setTargetType(tagTarget.getTargetType())
        .setTargetName(tagTarget.getTargetName())
        .setCreatedBy(tagTarget.getCreatedBy())
        .setCreatedDate(tagTarget.getCreatedDate())
        .setTargetCreatedBy(tagTarget.getTargetCreatedBy())
        .setTargetCreatedDate(tagTarget.getTargetCreatedDate());
  }

  public static GenericSpecification<WebTagTarget> getTagTargetSpecification(
      AppTagTargetFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("targetId", "createdDate")
        .orderByFields("targetId", "createdDate")
        .matchSearchFields("targetName")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<WebTagTarget> getTargetTagSpecification(
      AppTargetTagFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("tagId", "createdDate")
        .orderByFields("tagId", "createdDate")
        .matchSearchFields("tagName")
        .build();
    return new GenericSpecification<>(filters);
  }
}
