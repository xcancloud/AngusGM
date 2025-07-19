package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagTargetDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class WebTagTargetAssembler {

  public static WebTagTarget addDtoToTagTarget(Long tagId, WebTagTargetAddDto dto) {
    return new WebTagTarget().setTargetId(dto.getTargetId())
        .setTargetType(dto.getTargetType())
        .setTagId(tagId);
  }

  public static WebTagTargetDetailVo tagTargetToDetailVo(WebTagTarget tagTarget) {
    return new WebTagTargetDetailVo().setId(tagTarget.getId())
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

  public static GenericSpecification<WebTagTarget> getTagTargetSpecification(
      WebTagTargetFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("targetId", "createdDate")
        .orderByFields("targetId", "createdDate")
        .matchSearchFields("targetName")
        .build();
    return new GenericSpecification<>(filters);
  }

}
