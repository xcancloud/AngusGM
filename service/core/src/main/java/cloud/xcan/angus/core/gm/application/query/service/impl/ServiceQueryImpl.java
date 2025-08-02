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
import cloud.xcan.angus.core.gm.domain.service.ServiceSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of service query operations.
 * </p>
 * <p>
 * Manages service retrieval, validation, and API association.
 * Provides comprehensive service querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports service detail retrieval, paginated listing, API management,
 * resource queries, and service validation for comprehensive service administration.
 * </p>
 */
@Biz
@SummaryQueryRegister(name = "Service", table = "service", isMultiTenantCtrl = false,
    groupByColumns = {"created_date", "source", /*"api_doc_type",*/ "enabled"})
public class ServiceQueryImpl implements ServiceQuery {

  @Resource
  private ServiceRepo serviceRepo;
  @Resource
  private ServiceSearchRepo serviceSearchRepo;
  @Resource
  private ApiRepo apiRepo;
  @Resource
  private ServiceResourceApiRepo serviceResourceApiRepo;

  /**
   * <p>
   * Retrieves detailed service information by ID.
   * </p>
   * <p>
   * Fetches complete service record with API count association.
   * Throws ResourceNotFound exception if service does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves services with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Enriches results with API count information for comprehensive display.
   * </p>
   */
  @Override
  public Page<Service> list(GenericSpecification<Service> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Service>>() {

      @Override
      protected Page<Service> process() {
        Page<Service> servicePage = fullTextSearch
        ? serviceSearchRepo.find(spec.getCriteria(), pageable, Service.class, match)
        : serviceRepo.findAll(spec, pageable);
        setApiNum(servicePage.getContent());
        return servicePage;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves API list for specific service.
   * </p>
   * <p>
   * Returns all APIs associated with the specified service.
   * Validates service existence before retrieving APIs.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves resource list for services.
   * </p>
   * <p>
   * Returns service resources with optional authentication filtering.
   * Handles both specific service code and all services scenarios.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves resource API list for specific service.
   * </p>
   * <p>
   * Returns service resource APIs with optional authentication filtering.
   * Supports both all resources and specific resource name scenarios.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves service by ID.
   * </p>
   * <p>
   * Returns service by ID with existence validation.
   * Throws ResourceNotFound if service does not exist.
   * </p>
   */
  @Override
  public Service checkAndFind(Long id) {
    return serviceRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Service"));
  }

  /**
   * <p>
   * Validates and retrieves services by service codes.
   * </p>
   * <p>
   * Returns services by codes with existence validation.
   * Validates that all requested service codes exist.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves services by IDs with optional enabled check.
   * </p>
   * <p>
   * Returns services by IDs with existence validation.
   * Optionally validates that all services are enabled.
   * </p>
   */
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
      for (Service service : services) {
        assertTrue(service.getEnabled(), SERVICE_IS_DISABLED_T, new Object[]{service.getName()});
      }
    }
    return services;
  }

  /**
   * <p>
   * Validates service code for addition.
   * </p>
   * <p>
   * Ensures service code does not already exist when adding new service.
   * Throws ResourceExisted if service code already exists.
   * </p>
   */
  @Override
  public void checkAddServiceCode(Service service) {
    Service serviceDb = serviceRepo.findByCode(service.getCode()).orElse(null);
    assertResourceExisted(serviceDb, service.getCode(), "Service");
  }

  /**
   * <p>
   * Validates service code for update.
   * </p>
   * <p>
   * Ensures service code uniqueness when updating existing service.
   * Allows same service to keep its code during update.
   * </p>
   */
  @Override
  public void checkUpdateServiceCode(Service service) {
    if (nonNull(service.getCode())) {
      Service serviceDb = serviceRepo.findByCode(service.getCode()).orElse(null);
      assertResourceExisted(isNull(serviceDb) || serviceDb.getId().equals(service.getId()),
          service.getCode(), "Service");
    }
  }

  /**
   * <p>
   * Sets API count for service list.
   * </p>
   * <p>
   * Loads API counts and associates with services for complete information.
   * </p>
   */
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
