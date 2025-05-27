package cloud.xcan.angus.core.gm.application.query.service.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SERVICE_IS_DISABLED_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.commonlink.service.ServiceRepo;
import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.api.commonlink.service.ServiceResourceApi;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.service.ServiceResourceApiRepo;
import cloud.xcan.angus.core.jpa.repository.LongKeyCountSummary;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

@Biz
@SummaryQueryRegister(name = "Service", table = "service", isMultiTenantCtrl = false,
    groupByColumns = {"created_date", "source", /*"api_doc_type",*/ "enabled"})
public class ServiceQueryImpl implements ServiceQuery {

  @Resource
  private ServiceRepo serviceRepo;

  @Resource
  private ApiRepo apiRepo;

  @Resource
  private ServiceResourceApiRepo serviceResourceApiRepo;

  @Override
  public Service detail(Long id) {
    return new BizTemplate<Service>() {

      @Override
      protected Service process() {
        Service service = serviceRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "Service"));
        setApiNum(Collections.singletonList(service));
        return service;
      }
    }.execute();
  }

  @Override
  public Page<Service> find(Specification<Service> spec, Pageable pageable) {
    return new BizTemplate<Page<Service>>() {

      @Override
      protected Page<Service> process() {
        Page<Service> servicePage = serviceRepo.findAll(spec, pageable);
        setApiNum(servicePage.getContent());
        return servicePage;
      }
    }.execute();
  }

  @Override
  public List<Api> apiList(Long id) {
    return new BizTemplate<List<Api>>() {

      @Override
      protected List<Api> process() {
        Service service = detail(id);
        List<String> serviceCodes = new ArrayList<>();
        serviceCodes.add(service.getCode());
        return apiRepo.findAllByServiceCodeIn(serviceCodes);
      }
    }.execute();
  }

  @Override
  public List<Service> resourceList(String serviceCode, Boolean auth) {
    return new BizTemplate<List<Service>>(false) {
      List<Service> servicesDb;

      @Override
      protected void checkParams() {
        if (isNotEmpty(serviceCode)) {
          servicesDb = checkAndFind(List.of(serviceCode));
        }
      }

      @Override
      protected List<Service> process() {
        // Query all service when serviceCode is empty
        if (isEmpty(servicesDb)) {
          servicesDb = serviceRepo.findAll();
        }

        for (Service serviceDb : servicesDb) {
          List<ServiceResource> serviceResources = isNotEmpty(serviceCode) ?
              apiRepo.findServiceResourceByServiceCode(serviceCode) : apiRepo.findServiceResource();
          if (isNull(auth)) {
            serviceDb.setResources(serviceResources);
          } else {
            serviceDb.setResources(serviceResources.stream().filter(x ->
                auth != x.isIgnoredAuth()).collect(Collectors.toList()));
          }
        }
        return servicesDb;
      }
    }.execute();
  }

  @Transactional
  @Override
  public Service resourceApiList(String serviceCode, String resourceName, Boolean auth) {
    return new BizTemplate<Service>(false) {
      Service serviceDb;

      @Override
      protected void checkParams() {
        serviceDb = checkAndFind(List.of(serviceCode)).get(0);
      }

      @Override
      protected Service process() {
        List<ServiceResourceApi> serviceResourceApis = isEmpty(resourceName)
            ? serviceResourceApiRepo.findServiceResourceApiList(serviceCode)
            : serviceResourceApiRepo.findServiceResourceApiList(serviceCode, resourceName);
        if (isNull(auth)) {
          serviceDb.setResourceApis(serviceResourceApis);
        } else {
          serviceDb.setResourceApis(serviceResourceApis.stream().filter(x ->
              auth != x.isIgnoredAuth()).collect(Collectors.toList()));
        }
        return serviceDb;
      }
    }.execute();
  }

  @Override
  public Service checkAndFind(Long id) {
    return serviceRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Service"));
  }

  @Override
  public List<Service> checkAndFind(Collection<String> serviceCodes) {
    List<Service> services = serviceRepo.findByCodeIn(serviceCodes);
    assertResourceNotFound(isNotEmpty(services), serviceCodes.iterator().next(), "Service");

    if (serviceCodes.size() != services.size()) {
      for (Service service : services) {
        assertResourceNotFound(serviceCodes.contains(service.getCode()), service.getCode(),
            "Service");
      }
    }
    return services;
  }

  @Override
  public List<Service> checkAndFind(Collection<Long> ids, boolean checkEnabled) {
    List<Service> services = serviceRepo.findAllByIdIn(ids);
    assertResourceNotFound(isNotEmpty(services), ids.iterator().next(), "Service");

    if (ids.size() != services.size()) {
      for (Service service : services) {
        assertResourceNotFound(ids.contains(service.getId()), service.getId(), "Service");
      }
    }

    if (checkEnabled) {
      for (Service api : services) {
        assertTrue(api.getEnabled(), SERVICE_IS_DISABLED_T, new Object[]{api.getName()});
      }
    }
    return services;
  }

  @Override
  public void checkAddServiceCode(Service service) {
    Service serviceDb = serviceRepo.findByCode(service.getCode()).orElse(null);
    assertResourceExisted(serviceDb, service.getCode(), "Service");
  }

  @Override
  public void checkUpdateServiceCode(Service service) {
    if (nonNull(service.getCode())) {
      Service serviceDb = serviceRepo.findByCode(service.getCode()).orElse(null);
      assertResourceExisted(isNull(serviceDb) || serviceDb.getId().equals(service.getId()),
          service.getCode(), "Service");
    }
  }

  @Override
  public void setApiNum(List<Service> services) {
    if (isNotEmpty(services)) {
      Map<Long, Long> apisNumMap = apiRepo.countByFiltersAndGroup(Api.class,
              LongKeyCountSummary.class, null, "serviceId", "id").stream()
          .collect(Collectors.toMap(LongKeyCountSummary::getKey, LongKeyCountSummary::getTotal));
      for (Service service : services) {
        service.setApiNum(apisNumMap.containsKey(service.getId())
            ? apisNumMap.get(service.getId()) : 0);
      }
    }
  }
}
