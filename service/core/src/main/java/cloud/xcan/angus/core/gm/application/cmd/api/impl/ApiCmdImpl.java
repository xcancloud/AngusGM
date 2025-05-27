package cloud.xcan.angus.core.gm.application.cmd.api.impl;

import static cloud.xcan.angus.core.gm.application.converter.ApiConverter.parseSwaggerDocs;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.API_SWAGGER_PARSE_ERROR;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.API_SWAGGER_PARSE_ERROR_CODE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.API;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.utils.CoreUtils.batchCopyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.gm.application.cmd.api.ApiCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.system.SystemTokenCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthority;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthorityRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Biz
public class ApiCmdImpl extends CommCmd<Api, Long> implements ApiCmd {

  @Resource
  private ApiRepo apiRepo;

  @Resource
  private ApiQuery apiQuery;

  @Resource
  private ServiceQuery serviceQuery;

  @Resource
  private ApiAuthorityRepo apiAuthorityRepo;

  @Resource
  private SystemTokenCmd systemTokenCmd;

  @Resource
  private AppRepo appRepo;

  @Resource
  private AppFuncRepo appFuncRepo;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Api> apis, boolean saveOperationLog) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        // Set service information
        apiQuery.setServiceInfo(apis);

        // Save apis
        List<IdKey<Long, Object>> idKeys = batchInsert(apis, "name");

        // Save operation logs
        if (saveOperationLog) {
          operationLogCmd.addAll(API, apis, CREATED);
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Api> apis, boolean saveOperationLog) {
    new BizTemplate<Void>() {
      List<Api> apisDb;
      List<Service> servicesDb;

      @Override
      protected void checkParams() {
        // Check the apis existed
        Set<Long> apiIds = apis.stream().map(Api::getId).collect(Collectors.toSet());
        apisDb = apiQuery.checkAndFind(apiIds, false);
        // Check the associated service existed
        Set<Long> serviceIds = apis.stream().filter(Objects::nonNull)
            .map(Api::getServiceId).collect(Collectors.toSet());
        if (isNotEmpty(serviceIds)) {
          servicesDb = serviceQuery.checkAndFind(serviceIds, false);
        }
      }

      @Override
      protected Void process() {
        // Update services information
        updateServiceWhenChanged(apis, apisDb, servicesDb);

        // Save updated apis
        batchUpdateOrNotFound(apis);

        // Save operation logs
        if (saveOperationLog) {
          operationLogCmd.addAll(API, apis, CREATED);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> replace(List<Api> apis) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      List<Api> replaceApis;
      List<Api> replaceApisDb;

      @Override
      protected void checkParams() {
        // Check the apis existed
        replaceApis = apis.stream().filter(api -> nonNull(api.getId()))
            .collect(Collectors.toList());
        replaceApisDb = apiQuery.checkAndFind(replaceApis.stream().map(Api::getId)
            .collect(Collectors.toList()), false);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        List<Api> addApis = apis.stream().filter(api -> isNull(api.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addApis)) {
          idKeys.addAll(add(addApis, true));
        }

        if (isNotEmpty(replaceApis)) {
          Map<Long, Api> groupDbMap = replaceApisDb.stream()
              .collect(Collectors.toMap(Api::getId, x -> x));
          update(replaceApis.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, groupDbMap.get(x.getId()),
                  "serviceId", "serviceCode", "serviceName", "serviceEnabled",
                  "sync", "swaggerDeleted", "sync", "enabled"))
              .collect(Collectors.toList()), true);
          idKeys.addAll(replaceApis.stream().map(x -> IdKey.of(x.getId(), x.getName())).toList());
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Collection<Long> apiIds) {
    new BizTemplate<Void>() {
      List<Api> apisDb;

      @Override
      protected void checkParams() {
        apisDb = apiQuery.findAllById(apiIds);
      }

      @Override
      protected Void process() {
        if (isEmpty(apisDb)) {
          return null;
        }

        // Delete apis
        apiRepo.deleteByIdIn(apiIds);

        // Delete the associated apis with the applications and functions
        deleteAppAndFuncApis(apiIds);

        // Delete api authority
        apiAuthorityRepo.deleteByApiIdIn(apiIds);

        // Delete system token authorization
        systemTokenCmd.deleteByApiIdIn(apiIds);

        // Save operation logs
        operationLogCmd.addAll(API, apisDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Disabling/disabling will lead to the consistency problem of authorization data, which is
   * complex to handle. It is a good choice to only consider saving valid data for publishing
   * environment data, and consider discarding the api later.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<Api> apis) {
    new BizTemplate<Void>() {
      Set<Long> apiIds;
      List<Api> apisDb;

      @Override
      protected void checkParams() {
        // Check the apis existed
        apiIds = apis.stream().map(Api::getId).collect(Collectors.toSet());
        apisDb = apiQuery.checkAndFind(apiIds, false);
      }

      @Override
      protected Void process() {
        // Update enabled or disabled status
        batchUpdate0(batchCopyPropertiesIgnoreNull(apis, apisDb));

        // Sync the api enabled or disabled status to authority
        List<ApiAuthority> authorities = apiAuthorityRepo.findByApiIdIn(apiIds);
        if (isNotEmpty(authorities)) {
          Map<Long, Api> apiMap = apis.stream().collect(Collectors.toMap(Api::getId, x -> x));
          for (ApiAuthority authority : authorities) {
            authority.setApiEnabled(apiMap.get(authority.getApiId()).getEnabled());
          }
          apiAuthorityRepo.saveAll(authorities);
        }

        // NOOP:: Synchronously update the system token authorization resource status, alternative manual deletion of disabled apis.

        // Save operation logs
        operationLogCmd.addAll(
            API, apisDb.stream().filter(Api::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(
            API, apisDb.stream().filter(x -> !x.getEnabled()).toList(), DISABLED);
        return null;
      }
    }.execute();
  }

  @Override
  public void syncServiceApi(ServiceInstance instance, Service serviceDb) {
    // Parse the apis of services in the db from eureka
    List<Api> apis = parseApisFromSwagger(instance, serviceDb);

    // Save apis to database
    saveSyncApis(serviceDb.getCode(), apis);
  }

  @Override
  public void discoveryApiSync(List<ServiceInstance> instances, List<Service> servicesDb,
      List<String> discoveryServices) {
    Map<String, Service> serviceDbMap = servicesDb.stream()
        .filter(service -> discoveryServices.contains(service.getCode().toLowerCase()))
        .collect(Collectors.toMap(service -> service.getCode().toLowerCase(), service -> service));
    Map<String, ServiceInstance> instancesMap = instances.stream()
        .collect(Collectors.toMap(ServiceInstance::getServiceId, si -> si));
    for (Service serviceDb : serviceDbMap.values()) {
      // Parse the apis of services in the db from discovery
      List<Api> apis = parseApisFromSwagger(instancesMap.get(serviceDb.getCode()), serviceDb);
      ServiceInstance serviceInstance = instancesMap.get(serviceDb.getCode());
      if (isEmpty(apis) || isNull(serviceInstance)) {
        continue;
      }
      // Save apis to database
      saveSyncApis(serviceDb.getCode(), apis);
    }
  }

  @Override
  public void updateApiServiceStatus(List<Service> services) {
    Set<Long> enabledServiceIds = services.stream().filter(Service::getEnabled)
        .map(Service::getId).collect(Collectors.toSet());
    if (isNotEmpty(enabledServiceIds)) {
      apiRepo.updateServiceEnabled(enabledServiceIds, true);
    }

    Set<Long> disabledServiceIds = services.stream().filter(x -> !x.getEnabled())
        .map(Service::getId).collect(Collectors.toSet());
    if (isNotEmpty(disabledServiceIds)) {
      apiRepo.updateServiceEnabled(disabledServiceIds, false);
    }
  }

  @Override
  public void saveSyncApis(String serviceCode, List<Api> apis) {
    if (isEmpty(apis)) {
      return;
    }
    List<Api> apisDb = apiRepo.findAllByServiceCodeIn(singletonList(serviceCode));
    List<Api> updateApisCopy = new ArrayList<>(apis);
    List<Api> deletedApisCopy = new ArrayList<>(apis);
    List<Api> deletedApisDbCopy = new ArrayList<>(apisDb);
    //System.out.println(GsonUtils.toJson(apis));

    // If the database is empty, add it directly
    if (isEmpty(apisDb)) {
      add(apis, false);
      return;
    }

    // Update existed apis in database
    CoreUtils.retainAll(updateApisCopy, apisDb);
    if (isNotEmpty(updateApisCopy)) {
      for (Api apiDb : apisDb) {
        for (Api api : updateApisCopy) {
          if (apiDb.sameIdentityAs(api)) {
            api.setId(apiDb.getId());
          }
        }
      }
      update(updateApisCopy, false);
    }

    // Add new apis from swagger
    CoreUtils.removeAll(apis, updateApisCopy);
    if (isNotEmpty(apis)) {
      add(apis, false);
    }

    // Update the apis has deleted from swagger
    CoreUtils.removeAll(deletedApisDbCopy, deletedApisCopy);
    if (isNotEmpty(deletedApisDbCopy)) {
      update(deletedApisDbCopy.stream().map(api -> api.setSwaggerDeleted(true))
          .collect(Collectors.toList()), false);
    }
  }

  private void deleteAppAndFuncApis(Collection<Long> apiIds) {
    List<App> apps = appRepo.findAll();
    if (isNotEmpty(apps)) {
      List<App> updateApiApps = new ArrayList<>();
      for (App app : apps) {
        if (isNotEmpty(app.getApiIds()) && app.getApiIds().removeAll(apiIds)) {
          updateApiApps.add(app);
        }
        // Delete the api associated with the function
        updateFuncApis(app, apiIds);
      }
      if (isNotEmpty(updateApiApps)) {
        appRepo.saveAll(updateApiApps);
      }
    }
  }

  private void updateFuncApis(App app, Collection<Long> apiIds) {
    List<AppFunc> updateApiFuncs = new ArrayList<>();
    List<AppFunc> appFuncs = appFuncRepo.findAllByAppId(app.getId());
    if (isNotEmpty(appFuncs)) {
      for (AppFunc appFunc : appFuncs) {
        if (isNotEmpty(appFunc.getApiIds())) {
          if (appFunc.getApiIds().removeAll(apiIds)) {
            updateApiFuncs.add(appFunc);
          }
        }
      }
    }
    if (isNotEmpty(updateApiFuncs)) {
      appFuncRepo.saveAll(updateApiFuncs);
    }
  }

  @Override
  public List<Api> parseApisFromSwagger(ServiceInstance serviceInstance, Service serviceDb) {
    List<Api> apis = new ArrayList<>();
    try {
      HttpUrlConnectionSender httpSender = new HttpUrlConnectionSender();
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender, AuthConstant.SWAGGER_API_URL,
          ApiType.API);
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender,
          AuthConstant.SWAGGER_PUB_API_URL, ApiType.PUB_API);
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender,
          AuthConstant.SWAGGER_DOOR_API_URL, ApiType.DOOR_API);
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender,
          AuthConstant.SWAGGER_OPEN_API_TO_PRIVATE_URL, ApiType.OPEN_API_2P);
    } catch (Throwable e) {
      log.error("Parse swagger exception", e);
      throw BizException.of(API_SWAGGER_PARSE_ERROR_CODE, API_SWAGGER_PARSE_ERROR,
          new String[]{serviceDb.getCode()});
    }
    return apis;
  }

  private void updateServiceWhenChanged(List<Api> apis, List<Api> apisDb,
      List<Service> servicesDb) {
    Map<Long, Api> apiDbMap = apisDb.stream().collect(Collectors.toMap(Api::getId, x -> x));
    Map<Long, Service> serviceDbMap = servicesDb.stream()
        .collect(Collectors.toMap(Service::getId, x -> x));
    for (Api api : apis) {
      if (nonNull(api.getServiceId())
          && !api.getServiceId().equals(apiDbMap.get(api.getId()).getServiceId())) {
        Service serviceDb = serviceDbMap.get(api.getServiceId());
        api.setServiceCode(serviceDb.getCode()).setServiceName(serviceDb.getName());
      }
    }
  }

  @Override
  protected BaseRepository<Api, Long> getRepository() {
    return this.apiRepo;
  }
}
