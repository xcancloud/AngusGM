package cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler;

import static cloud.xcan.angus.core.utils.BeanFieldUtils.getNullPropertyNames;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTagUpdateDto;
import cloud.xcan.angus.api.gm.app.vo.AppTagVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;


public class AppTagAssembler {

  public static WebTag updateDtoToDomain(AppTagUpdateDto dto) {
    WebTag webTag = new WebTag();
    webTag.setId(dto.getId());
    BeanUtils.copyProperties(dto, webTag, getNullPropertyNames(dto));
    return webTag;
  }

  public static AppTagVo toListVo(WebTag webTag) {
    return new AppTagVo()
        .setId(webTag.getId())
        .setName(webTag.getName())
        .setCreatedBy(webTag.getCreatedBy())
        .setCreatedDate(webTag.getCreatedDate());
  }

  public static Specification<WebTag> getSpecification(AppTagFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        //.subTableFields("shelfClassification")
        .matchSearchFields("name")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
