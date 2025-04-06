package cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler;


import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiSearchDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiDetailVo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiInfoVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;


public class ApiAssembler {

  public static Api addDtoToDomain(ApiAddDto dto) {
    return new Api()
        .setServiceId(dto.getServiceId())
        .setName(dto.getName())
        .setCode(dto.getCode())
        .setUri(dto.getUri())
        .setMethod(dto.getMethod())
        .setType(dto.getType())
        .setDescription(dto.getDescription())
        .setResourceName(dto.getResourceName())
        .setResourceDescription(dto.getResourceDescription())
        .setEnabled(true)
        .setSync(false);
  }

  public static Api updateDtoToDomain(ApiUpdateDto dto) {
    return new Api().setName(dto.getName())
        .setCode(dto.getCode())
        .setUri(dto.getUri())
        .setMethod(dto.getMethod())
        .setType(dto.getType())
        .setDescription(dto.getDescription())
        .setResourceName(dto.getResourceName())
        .setResourceDescription(dto.getResourceDescription());
  }

  public static Api enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new Api().setId(dto.getId()).setEnabled(dto.getEnabled());
  }

  public static Api replaceDtoToDomain(ApiReplaceDto dto) {
    return new Api()
        .setId(dto.getId())
        .setServiceId(isNull(dto.getId()) ? dto.getServiceId() : null)
        .setName(dto.getName())
        .setCode(dto.getCode())
        .setUri(dto.getUri())
        .setMethod(dto.getMethod())
        .setType(dto.getType())
        .setDescription(dto.getDescription())
        .setResourceName(dto.getResourceName())
        .setResourceDescription(dto.getResourceDescription());
  }

  public static ApiDetailVo toApiDetailVo(Api api) {
    return new ApiDetailVo().setId(api.getId())
        .setCode(api.getCode())
        .setName(api.getName())
        .setDescription(api.getDescription())
        .setMethod(api.getMethod())
        .setResourceName(api.getResourceName())
        .setResourceDescription(api.getResourceDescription())
        .setEnabled(api.getEnabled())
        .setServiceId(api.getServiceId())
        .setServiceCode(api.getServiceCode())
        .setServiceName(api.getServiceName())
        .setType(api.getType())
        .setUri(api.getUri())
        .setCreatedBy(api.getCreatedBy())
        .setCreatedDate(api.getCreatedDate())
        //.setUserName() // Auto join
        .setLastModifiedBy(api.getLastModifiedBy())
        .setLastModifiedDate(api.getLastModifiedDate());
  }

  public static ApiInfoVo toInfo(Api api) {
    return new ApiInfoVo().setId(api.getId())
        .setCode(api.getCode())
        .setName(api.getName())
        .setEnabled(api.getEnabled())
        .setDescription(api.getDescription())
        .setResourceName(api.getResourceName())
        .setResourceDescription(api.getResourceDescription());
  }

  public static Specification<Api> getSpecification(ApiFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code", "serviceName", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(ApiSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code", "serviceName", "description")
        .orderByFields("id", "createdDate")
        .build();
  }
}
