package cloud.xcan.angus.core.gm.application.query.api.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.API_IS_DISABLED_T;
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
import cloud.xcan.angus.core.gm.domain.api.ApiSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of API query operations.
 * </p>
 * <p>
 * Manages API retrieval, validation, and service information enrichment.
 * Provides comprehensive API querying with full-text search support.
 * </p>
 * <p>
 * Supports API detail retrieval, paginated listing, validation checks,
 * and service information association for API management.
 * </p>
 */
@Biz
@SummaryQueryRegister(name = "Api", table = "api", isMultiTenantCtrl = false,
    groupByColumns = {"created_date", "method", "type", "enabled"})
public class ApiQueryImpl implements ApiQuery {

  @Resource
  private ApiRepo apiRepo;
  @Resource
  private ApiSearchRepo apiSearchRepo;
  @Resource
  private ServiceQuery serviceQuery;

  /**
   * <p>
   * Retrieves detailed API information by ID.
   * </p>
   * <p>
   * Fetches complete API record with all associated information.
   * Throws ResourceNotFound exception if API does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves paginated list of APIs with optional full-text search.
   * </p>
   * <p>
   * Supports both regular database queries and full-text search operations.
   * Returns filtered and paginated API results based on specification.
   * </p>
   */
  @Override
  public Page<Api> list(GenericSpecification<Api> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Api>>() {

      @Override
      protected Page<Api> process() {
        return fullTextSearch
            ? apiSearchRepo.find(spec.getCriteria(), pageable, Api.class, match)
            : apiRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves multiple APIs by their IDs.
   * </p>
   * <p>
   * Fetches API records for the specified collection of IDs.
   * Returns null if the collection is empty.
   * </p>
   */
  @Override
  public List<Api> findAllById(Collection<Long> ids) {
    if (isEmpty(ids)) {
      return null;
    }
    return apiRepo.findAllById(ids);
  }

  /**
   * <p>
   * Validates and retrieves API by ID with enabled status check.
   * </p>
   * <p>
   * Verifies API exists and is enabled. Throws appropriate exceptions
   * for missing or disabled APIs.
   * </p>
   */
  @Override
  public Api checkAndFind(Long id) {
    Api apiDb = apiRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Api"));
    assertTrue(apiDb.getEnabled(), API_IS_DISABLED_T, new Object[]{apiDb.getName()});
    return apiDb;
  }

  /**
   * <p>
   * Validates and retrieves multiple APIs by IDs with optional enabled status check.
   * </p>
   * <p>
   * Verifies all APIs exist and optionally checks enabled status.
   * Validates complete collection match and throws appropriate exceptions.
   * </p>
   */
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

  /**
   * <p>
   * Validates APIs belong to a specific service.
   * </p>
   * <p>
   * Verifies that all specified API IDs belong to the given service ID.
   * Throws ResourceNotFound exception if any API is not found or doesn't belong to the service.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves service resources by resource names.
   * </p>
   * <p>
   * Verifies that all specified resource names exist in the system.
   * Returns grouped service resources by resource name.
   * </p>
   */
  @Override
  public Map<String, List<ServiceResource>> checkAndFindResource(Collection<String> resources) {
    List<ServiceResource> resourceNames = apiRepo.findServiceResourceByResourceNameIn(resources);
    resources.removeAll(resourceNames.stream().map(ServiceResource::getResourceName)
        .collect(Collectors.toSet()));
    if (isNotEmpty(resources)) {
      throw ResourceNotFound.of(resources.iterator().next(), "Resource");
    }
    return resourceNames.stream().collect(Collectors.groupingBy(ServiceResource::getResourceName));
  }

  /**
   * <p>
   * Enriches APIs with service information.
   * </p>
   * <p>
   * Associates APIs with their corresponding service details including
   * service name, code, and enabled status for comprehensive API management.
   * </p>
   */
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
