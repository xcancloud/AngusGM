package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;


public class WebTagAssembler {

  public static WebTag addDtoToDomain(WebTagAddDto dto) {
    return new WebTag().setName(dto.getName()).setDescription(dto.getDescription());
  }

  public static WebTag updateDtoToDomain(WebTagUpdateDto dto) {
    return new WebTag().setId(dto.getId()).setName(dto.getName())
        .setDescription(dto.getDescription());
  }

  public static WebTagDetailVo domainToDetailVo(WebTag tag) {
    return new WebTagDetailVo().setId(tag.getId()).setName(tag.getName())
        .setDescription(tag.getDescription())
        //.setTenantId(tag.getTenantId())
        .setCreatedBy(tag.getCreatedBy())
        .setCreatedDate(tag.getCreatedDate())
        .setLastModifiedBy(tag.getLastModifiedBy())
        .setLastModifiedDate(tag.getLastModifiedDate());
  }

  public static GenericSpecification<WebTag> getSpecification(WebTagFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate")
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

}
