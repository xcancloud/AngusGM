package cloud.xcan.angus.core.gm.application.cmd.service.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SERVICE_EUREKA_NOT_EXISTED_T;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.commonlink.service.ServiceRepo;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.api.ApiCmd;
import cloud.xcan.angus.core.gm.application.cmd.authority.ApiAuthorityCmd;
import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of service command operations.
 * </p>
 * <p>
 * Manages service lifecycle including creation, updates, deletion, and synchronization with service
 * discovery systems like Eureka.
 * </p>
 * <p>
 * Provides comprehensive service management functionality with API association, status management,
 * and discovery integration.
 * </p>
 */
@org.springframework.stereotype.Service
public class ServiceCmdImpl extends CommCmd<Service, Long> implements ServiceCmd {

  @Resource
  private ApiRepo apiRepo;
  @Resource
  private ApiQuery apiQuery;
  @Resource
  private ApiCmd apiCmd;
  @Resource
  private ServiceRepo serviceRepo;
  @Resource
  private ServiceQuery serviceQuery;
  @Resource
  private ApiAuthorityCmd authorityCmd;
  @Resource
  private DiscoveryClient discoveryClient;

  /**
   * <p>
   * Creates a new service with validation.
   * </p>
   * <p>
   * Validates service code uniqueness and creates the service in the database.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Service service) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Verify service code uniqueness
        serviceQuery.checkAddServiceCode(service);
      }

      @Override
      protected IdKey<Long, Object> process() {
        return insert(service, "code");
      }
    }.execute();
  }

  /**
   * <p>
   * Updates an existing service with validation.
   * </p>
   * <p>
   * Validates service existence and code uniqueness, then updates the service and synchronizes
   * related API information.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Service service) {
    new BizTemplate<Void>() {
      Service serviceDb;

      @Override
      protected void checkParams() {
        // Verify service exists
        serviceDb = serviceQuery.checkAndFind(service.getId());
        // Verify service code uniqueness
        serviceQuery.checkUpdateServiceCode(service);
      }

      @Override
      protected Void process() {
        updateOrNotFound0(service);
        updateApiServiceNameAndCode(service, serviceDb);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces a service with new or updated data.
   * </p>
   * <p>
   * Creates new service if ID is null, otherwise updates existing service. Preserves immutable
   * fields during updates.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(Service service) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Service serviceDb;

      @Override
      protected void checkParams() {
        if (nonNull(service.getId())) {
          // Verify service exists
          serviceDb = serviceQuery.checkAndFind(service.getId());
          // Verify service code uniqueness
          serviceQuery.checkUpdateServiceCode(service);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(service.getId())) {
          return add(service);
        }

        // Preserve immutable fields during update
        serviceRepo.save(copyPropertiesIgnoreTenantAuditing(service, serviceDb,
            "code", "source", "enabled"));

        updateApiServiceNameAndCode(service, serviceDb);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Enables or disables services and synchronizes related components.
   * </p>
   * <p>
   * Updates service status and synchronizes API status and authority status to maintain consistency
   * across the system.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<Service> services) {
    new BizTemplate<Void>() {
      Set<Long> serviceIds;

      @Override
      protected void checkParams() {
        // Verify services exist
        serviceIds = services.stream().map(Service::getId).collect(Collectors.toSet());
        serviceQuery.checkAndFind(serviceIds, false);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(services);

        // Synchronize API service status
        apiCmd.updateApiServiceStatus(services);

        // Synchronize authority service status
        authorityCmd.updateAuthorityServiceStatus(serviceIds, services);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes services and their associated data.
   * </p>
   * <p>
   * Removes services and synchronously deletes associated APIs and authorities to maintain data
   * consistency.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Delete services
        serviceRepo.deleteByIdIn(ids);

        // Synchronously delete associated APIs and authorities
        for (Long id : ids) {
          List<Long> serviceApiIds = apiRepo.findIdByServiceId(id);
          if (isNotEmpty(serviceApiIds)) {
            apiCmd.delete(serviceApiIds);
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Adds APIs to services.
   * </p>
   * <p>
   * Delegates to API command to add multiple APIs to services.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> apiAdd(List<Api> apis) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {

      @Override
      protected List<IdKey<Long, Object>> process() {
        return apiCmd.add(apis, false);
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes APIs from a specific service.
   * </p>
   * <p>
   * Validates API ownership and removes specified APIs from the service.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void apiDelete(Long serviceId, HashSet<Long> apiIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Verify APIs belong to the service
        apiQuery.checkByServiceId(serviceId, apiIds);
      }

      @Override
      protected Void process() {
        apiCmd.delete(apiIds);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Synchronizes service APIs from service discovery.
   * </p>
   * <p>
   * Retrieves service instances from discovery client and synchronizes their APIs with the
   * database.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void syncServiceApi(String serviceCode) {
    new BizTemplate<Void>() {
      List<ServiceInstance> serviceInstances;
      Service serviceDb;

      @Override
      protected void checkParams() {
        // Verify service exists in database
        serviceDb = serviceRepo.findByCode(serviceCode)
            .orElseThrow(() -> ResourceNotFound.of(serviceCode, "Service"));

        // Get service instances from discovery
        serviceInstances = discoveryClient.getInstances(serviceCode.toLowerCase());
        assertResourceNotFound(serviceInstances, SERVICE_EUREKA_NOT_EXISTED_T,
            new Object[]{serviceCode});
      }

      @Override
      protected Void process() {
        apiCmd.syncServiceApi(serviceInstances.get(0), serviceDb);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Synchronizes all services with discovery center.
   * </p>
   * <p>
   * Discovers all services from the discovery center and synchronizes their APIs with the database
   * for services that exist in both systems.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void discoveryApiSync() {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Get all service codes from discovery center
        List<String> discoveryServices = discoveryClient.getServices();
        if (isEmpty(discoveryServices)) {
          // Discovery service is empty
          return null;
        }

        List<Service> serviceDbs = serviceRepo.findAll();
        if (isEmpty(serviceDbs)) {
          // No services exist in database
          return null;
        }

        // Find services that exist in both discovery and database
        List<String> serviceCodesInDb = serviceDbs.stream().map(Service::getCode)
            .map(String::toLowerCase).collect(Collectors.toList());
        discoveryServices.retainAll(serviceCodesInDb);
        if (isEmpty(discoveryServices)) {
          // Services in database are not registered with discovery
          return null;
        }

        // Query service instances from discovery
        List<ServiceInstance> instances = new ArrayList<>();
        discoveryServices.forEach(code -> {
          List<ServiceInstance> serviceInstances = discoveryClient.getInstances(code);
          if (isNotEmpty(serviceInstances)) {
            instances.add(serviceInstances.get(0));
          }
        });
        apiCmd.discoveryApiSync(instances, serviceDbs, discoveryServices);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates API service name and code when service information changes.
   * </p>
   * <p>
   * Synchronizes service name and code changes to associated APIs to maintain consistency.
   * </p>
   */
  private void updateApiServiceNameAndCode(Service service, Service serviceDb) {
    // Update APIs when service name or code changes
    if (service.getId() != null) {
      if (isNotEmpty(service.getCode()) && !service.getCode().equals(serviceDb.getCode())) {
        apiRepo.updateServiceCodeByServiceId(service.getId(), service.getCode());
      }
      if (isNotEmpty(service.getName()) && !service.getName().equals(serviceDb.getName())) {
        apiRepo.updateServiceNameByServiceId(service.getId(), service.getName());
      }
    }
  }

  @Override
  protected BaseRepository<Service, Long> getRepository() {
    return this.serviceRepo;
  }
}
