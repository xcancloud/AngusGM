package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import java.util.HashSet;
import java.util.Set;

/**
 * Interface assembler
 */
public class InterfacesAssembler {

  /**
   * Convert Domain to DetailVo
   */
  public static cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo toDetailVo(Interface inter) {
    cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo vo = 
        new cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo();
    vo.setId(inter.getId());
    vo.setServiceName(inter.getServiceName());
    vo.setCode(inter.getCode());
    vo.setPath(inter.getPath());
    vo.setMethod(inter.getMethod());
    vo.setSummary(inter.getSummary());
    vo.setDescription(inter.getDescription());
    vo.setTags(inter.getTags());
    vo.setParameters(inter.getParameters());
    vo.setResponses(inter.getResponses());
    vo.setDeprecated(nullSafe(inter.getDeprecated(), false));
    vo.setVersion(inter.getVersion());
    vo.setLastSyncTime(inter.getLastSyncTime());
    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo toListVo(Interface inter) {
    cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo vo = 
        new cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo();
    vo.setId(inter.getId());
    vo.setServiceName(inter.getServiceName());
    vo.setCode(inter.getCode());
    vo.setPath(inter.getPath());
    vo.setMethod(inter.getMethod());
    vo.setSummary(inter.getSummary());
    vo.setDescription(inter.getDescription());
    vo.setTags(inter.getTags());
    vo.setDeprecated(nullSafe(inter.getDeprecated(), false));
    vo.setVersion(inter.getVersion());
    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static GenericSpecification<Interface> getSpecification(InterfaceFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "lastSyncTime")
        .orderByFields("id", "createdDate", "modifiedDate", "path", "name")
        .matchSearchFields("path", "description", "summary", "name")
        .build();
    
    // Add method filter
    if (dto.getMethod() != null) {
      filters.add(new SearchCriteria("method", dto.getMethod().name(), SearchOperation.EQUAL));
    }
    
    // Add deprecated filter
    if (dto.getDeprecated() != null) {
      filters.add(new SearchCriteria("deprecated", dto.getDeprecated(), SearchOperation.EQUAL));
    }
    
    // Add tags filter
    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      for (String tag : dto.getTags()) {
        filters.add(new SearchCriteria("tags", tag, SearchOperation.MATCH));
      }
    }
    
    return new GenericSpecification<>(filters);
  }
}
