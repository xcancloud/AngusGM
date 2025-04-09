package cloud.xcan.angus.core.gm.application.query.api.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.API_IS_DISABLED_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
@SummaryQueryRegister(name = "Api", table = "api", isMultiTenantCtrl = false,
    groupByColumns = {"created_date", "method", "type", "enabled"})
public class ApiQueryImpl implements ApiQuery {

  @Resource
  private ApiRepo apiRepo;

  @Resource
  private ServiceQuery serviceQuery;

  @Override
  public Api detail(Long id) {
    return new BizTemplate<Api>() {

      @Override
      protected Api process() {
        return apiRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "Api"));
      }
    }.execute();
  }

  @Override
  public Page<Api> list(Specification<Api> spec, Pageable pageable) {
    return new BizTemplate<Page<Api>>() {

      @Override
      protected Page<Api> process() {
        return apiRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<Api> findAllById(Collection<Long> ids) {
    if (isEmpty(ids)) {
      return null;
    }
    return apiRepo.findAllById(ids);
  }

  @Override
  public Api checkAndFind(Long id) {
    Api groupDb = apiRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Api"));
    assertTrue(groupDb.getEnabled(), API_IS_DISABLED_T, new Object[]{groupDb.getName()});
    return groupDb;
  }

  @Override
  public List<Api> checkAndFind(Collection<Long> apiIds, boolean checkEnabled) {
    if (isEmpty(apiIds)) {
      return null;
    }
    List<Api> apis = apiRepo.findByIdIn(apiIds);
    assertResourceNotFound(isNotEmpty(apis), apiIds.iterator().next(), "Api");
    if (apiIds.size() != apis.size()) {
      for (Api api : apis) {
        assertResourceNotFound(apiIds.contains(api.getId()), api.getId(), "Api");
      }
    }
    if (checkEnabled) {
      for (Api api : apis) {
        assertTrue(api.getEnabled(), API_IS_DISABLED_T, new Object[]{api.getName()});
      }
    }
    return apis;
  }

  @Override
  public void checkByServiceId(Long serviceId, HashSet<Long> apiIds) {
    List<Api> apis = apiRepo.findAllByServiceIdAndIdIn(serviceId, apiIds);
    assertResourceNotFound(isNotEmpty(apis), apiIds.iterator().next(), "Api");

    if (apiIds.size() != apis.size()) {
      for (Api api : apis) {
        assertResourceNotFound(apiIds.contains(api.getId()), api.getId(), "Api");
      }
    }
  }

  @Override
  public Map<String, List<ServiceResource>> checkAndFindResource(Collection<String> resources) {
    List<ServiceResource> resourceNames = apiRepo.findServiceResourceByResourceNameIn(resources);
    resources.removeAll(resourceNames.stream().map(ServiceResource::getResourceName).collect(
        Collectors.toSet()));
    if (isNotEmpty(resources)) {
      throw ResourceNotFound.of(resources.iterator().next(), "Resource");
    }
    return resourceNames.stream().collect(Collectors.groupingBy(ServiceResource::getResourceName));
  }

  @Override
  public void setServiceInfo(List<Api> apis) {
    List<Service> servicesDb = serviceQuery.checkAndFind(apis.stream().map(Api::getServiceId)
        .collect(Collectors.toSet()), false);
    Map<Long, Service> serviceDbMap = servicesDb.stream()
        .collect(Collectors.toMap(Service::getId, s -> s));
    for (Api api : apis) {
      String resourceDescription = nullSafe(api.getResourceDescription(), api.getResourceName());
      api.setEnabled(true).setResourceDescription(resourceDescription);
      // Update service status
      Service service = serviceDbMap.get(api.getServiceId());
      api.setServiceEnabled(service.getEnabled());
      api.setServiceName(service.getName());
      api.setServiceCode(service.getCode());
    }
  }
}
