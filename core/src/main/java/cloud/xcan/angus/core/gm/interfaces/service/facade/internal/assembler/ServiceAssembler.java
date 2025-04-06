package cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler;


import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.api.commonlink.service.ServiceResourceApi;
import cloud.xcan.angus.api.commonlink.service.ServiceSource;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiBaseVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceSearchDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ResourceApiVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ResourceVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceApiVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceResourceVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;


public class ServiceAssembler {

  public static Service addDtoToDomain(ServiceAddDto dto) {
    return new Service()
        .setName(dto.getName())
        .setCode(dto.getCode())
        .setDescription(stringSafe(dto.getDescription()))
        .setSource(nullSafe(dto.getSource(), ServiceSource.BACK_ADD))
        .setEnabled(nullSafe(dto.getEnabled(), true))
        .setRoutePath(stringSafe(dto.getRoutePath()))
        .setUrl(dto.getUrl())
        .setHealthUrl(stringSafe(dto.getHealthUrl()))
        .setApiDocUrl(stringSafe(dto.getApiDocUrl()));
  }

  public static Service updateDtoToDomain(ServiceUpdateDto dto) {
    return new Service().setId(dto.getId())
        .setName(dto.getName())
        //.setCode(dto.getCode()) // Modify not allowed
        .setDescription(dto.getDescription())
        .setSource(dto.getSource())
        .setRoutePath(dto.getRoutePath())
        //.setEnabled(dto.getEnabled()) // Modify not allowed
        .setUrl(dto.getUrl())
        .setHealthUrl(dto.getHealthUrl())
        .setApiDocUrl(dto.getApiDocUrl());
  }

  public static Service replaceDtoToDomain(ServiceReplaceDto dto) {
    return new Service().setId(dto.getId())
        .setName(dto.getName())
        // Modify not allowed
        .setCode(isNull(dto.getId()) ? dto.getCode() : null)
        .setDescription(stringSafe(dto.getDescription()))
        // Modify not allowed
        .setSource(isNull(dto.getId()) ? nullSafe(dto.getSource(), ServiceSource.BACK_ADD) : null)
        .setRoutePath(stringSafe(dto.getRoutePath()))
        // Modify not allowed
        .setEnabled(isNull(dto.getId()) ? nullSafe(dto.getEnabled(), true) : null)
        .setUrl(dto.getUrl())
        .setHealthUrl(stringSafe(dto.getHealthUrl()))
        .setApiDocUrl(stringSafe(dto.getApiDocUrl()));
  }

  public static Service enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new Service().setId(dto.getId())
        .setEnabled(dto.getEnabled());
  }

  public static Api addApiDtoToDomain(ServiceApiAddDto dto) {
    return new Api().setName(dto.getName())
        .setCode(dto.getCode())
        .setUri(dto.getUri())
        .setMethod(dto.getMethod())
        .setType(dto.getType())
        .setDescription(stringSafe(dto.getDescription()))
        .setResourceName(stringSafe(dto.getResourceName()))
        .setServiceId(dto.getServiceId())
        .setSync(false)
        .setEnabled(nullSafe(dto.getEnabled(), true));
  }

  public static ServiceVo toServiceDetailVo(Service service) {
    return new ServiceVo()
        .setId(service.getId())
        .setName(service.getName())
        .setCode(service.getCode())
        .setUrl(service.getUrl())
        .setSource(service.getSource())
        .setHealthUrl(service.getHealthUrl())
        .setEnabled(service.getEnabled())
        .setDescription(service.getDescription())
        //.setStatus(service.getStatus())
        .setApiNum(service.getApiNum())
        .setCreatedBy(service.getCreatedBy())
        .setCreatedDate(service.getCreatedDate())
        .setLastModifiedBy(service.getLastModifiedBy())
        .setLastModifiedDate(service.getLastModifiedDate());
  }

  public static ServiceApiVo toServiceApiVo(Api api) {
    return new ServiceApiVo().setId(api.getId())
        .setName(api.getName())
        .setUri(api.getUri())
        .setCode(api.getCode())
        .setMethod(api.getMethod())
        .setType(api.getType())
        .setResourceName(api.getResourceName())
        .setServiceName(api.getServiceName())
        .setDescription(api.getDescription())
        .setEnabled(api.getEnabled());
  }

  public static List<ServiceResourceVo> toServiceResourceVo(List<Service> services) {
    return services.stream().map(service ->
            new ServiceResourceVo().setServiceId(service.getId())
                .setServiceName(service.getName())
                .setServiceCode(service.getCode())
                .setServiceDesc(service.getDescription())
                .setServiceEnabled(service.getEnabled())
                .setResources(toResourceVo(service.getResources())))
        .collect(Collectors.toList());
  }

  public static List<ResourceVo> toResourceVo(List<ServiceResource> resources) {
    return isEmpty(resources) ? null : resources.stream()
        .map(resource -> new ResourceVo()
            .setResourceName(resource.getResourceName())
            .setResourceDesc(isNotEmpty(resource.getResourceDesc())
                ? resource.getResourceDesc() : resource.getResourceName()))
        .collect(Collectors.toList());
  }

  public static List<ResourceApiVo> toServiceResourceApiVo(Service resourceApis) {
    if (isEmpty(resourceApis) && isEmpty(resourceApis.getResourceApis())) {
      return null;
    }
    List<ResourceApiVo> apiVos = new ArrayList<>();
    Map<String, List<ServiceResourceApi>> resourceApiVos = resourceApis.getResourceApis()
        .stream().collect(Collectors.groupingBy(ServiceResourceApi::getResourceName));
    for (String key : resourceApiVos.keySet()) {
      List<ServiceResourceApi> resourceApi = resourceApiVos.get(key);
      if (isNotEmpty(resourceApi)) {
        apiVos.add(new ResourceApiVo().setResourceName(resourceApi.get(0).getResourceName())
            .setResourceDesc(resourceApi.get(0).getResourceDesc())
            .setApis(toApiInfos(resourceApi)));
      }
    }
    return apiVos;
  }

  public static List<ApiBaseVo> toApiInfos(List<ServiceResourceApi> apis) {
    // When ServiceResourceApi is interface:: Throw java.lang.IllegalArgumentException: Projection type must be an interface!
    List<ApiBaseVo> baseVos = new ArrayList<>();
    for (ServiceResourceApi api : apis) {
      baseVos.add(new ApiBaseVo().setId(api.getApiId())
          .setCode(api.getApiCode())
          .setName(api.getApiName())
          .setDescription(api.getApiDescription())
          .setEnabled(api.getApiEnabled()));
    }
    return baseVos;
  }

  public static Specification<Service> getSpecification(ServiceFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        //.subTableFields("shelfClassification")
        .matchSearchFields("name", "code", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(ServiceSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code", "description")
        .orderByFields("id", "createdDate")
        .build();
  }

}
