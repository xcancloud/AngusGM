package cloud.xcan.angus.core.gm.application.cmd.service.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SERVICE_DISCOVERY_NOT_EXISTED_T;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.commonlink.service.ServiceRepo;
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Service service) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Check the service code existed
        serviceQuery.checkAddServiceCode(service);
      }

      @Override
      protected IdKey<Long, Object> process() {
        return insert(service, "code");
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Service service) {
    new BizTemplate<Void>() {
      Service serviceDb;

      @Override
      protected void checkParams() {
        // Check the service existed
        serviceDb = serviceQuery.checkAndFind(service.getId());
        // Check the service code existed
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(Service service) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Service serviceDb;

      @Override
      protected void checkParams() {
        if(nonNull(service.getId())){
          // Check the updated services existed
          serviceDb = serviceQuery.checkAndFind(service.getId());
          // Check update code is not repeated
          serviceQuery.checkUpdateServiceCode(service);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(service.getId())) {
          return add(service);
        }

        // Do not replace source, enabled and tenant auditing
        serviceRepo.save(copyPropertiesIgnoreTenantAuditing(service, serviceDb,
            "code", "source", "enabled"));

        updateApiServiceNameAndCode(service, serviceDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<Service> services) {
    new BizTemplate<Void>() {
      Set<Long> serviceIds;

      @Override
      protected void checkParams() {
        // Check the service existed
        serviceIds = services.stream().map(Service::getId).collect(Collectors.toSet());
        serviceQuery.checkAndFind(serviceIds, false);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(services);

        apiCmd.updateApiServiceStatus(services);

        // Sync update authority status
        authorityCmd.updateAuthorityServiceStatus(serviceIds, services);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Delete services
        serviceRepo.deleteByIdIn(ids);

        // Synchronous deletion apis and authority
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> apiAdd(List<Api> apis) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {

      @Override
      protected List<IdKey<Long, Object>> process() {
        return apiCmd.add(apis);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void apiDelete(Long serviceId, HashSet<Long> apiIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        apiQuery.checkByServiceId(serviceId, apiIds);
      }

      @Override
      protected Void process() {
        apiCmd.delete(apiIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void syncServiceApi(String serviceCode) {
    new BizTemplate<Void>() {
      List<ServiceInstance> serviceInstances;
      Service serviceDb;

      @Override
      protected void checkParams() {
        // Check the service existed
        serviceDb = serviceRepo.findByCode(serviceCode)
            .orElseThrow(() -> ResourceNotFound.of(serviceCode, "Service"));

        // Get registry service
        serviceInstances = discoveryClient.getInstances(serviceCode.toLowerCase());
        assertResourceNotFound(serviceInstances, SERVICE_DISCOVERY_NOT_EXISTED_T,
            new Object[]{serviceCode});
      }

      @Override
      protected Void process() {
        apiCmd.syncServiceApi(serviceInstances.get(0), serviceDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void discoveryApiSync() {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Get all service codes(LowerCase) of the discovery center
        List<String> discoveryServices = discoveryClient.getServices();
        if (isEmpty(discoveryServices)) {
          // The discovery service is empty
          return null;
        }

        List<Service> serviceDbs = serviceRepo.findAll();
        if (isEmpty(serviceDbs)) {
          // Not existed services in the database
          return null;
        }

        List<String> serviceCodesInDb = serviceDbs.stream().map(Service::getCode)
            .map(String::toLowerCase).collect(Collectors.toList());
        discoveryServices.retainAll(serviceCodesInDb);
        if (isEmpty(discoveryServices)) {
          // The service in the datasource is not registered with discovery
          return null;
        }

        // Query the service instance in discovery
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

  private void updateApiServiceNameAndCode(Service service, Service serviceDb) {
    // Modify api when the name or code of service is changed
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
