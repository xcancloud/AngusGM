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

/**
 * Implementation of API command operations for managing API endpoints and their lifecycle.
 * 
 * <p>This class provides comprehensive functionality for API management including:</p>
 * <ul>
 *   <li>Creating, updating, and deleting API endpoints</li>
 *   <li>Synchronizing APIs from service discovery and Swagger documentation</li>
 *   <li>Managing API enable/disable states</li>
 *   <li>Handling API authority and authorization</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures data consistency across related entities such as applications, 
 * functions, and authorization policies when APIs are modified or deleted.</p>
 */
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

  /**
   * Creates new API endpoints and associates them with services.
   * 
   * <p>This method performs the following operations:</p>
   * <ul>
   *   <li>Sets service information for each API</li>
   *   <li>Batch inserts APIs with validation</li>
   *   <li>Records operation logs for audit purposes</li>
   * </ul>
   * 
   * @param apis List of API entities to create
   * @param saveOperationLog Whether to record operation logs
   * @return List of created API identifiers with names
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Api> apis, boolean saveOperationLog) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        // Set service information for each API
        apiQuery.setServiceInfo(apis);

        // Save APIs with batch insertion
        List<IdKey<Long, Object>> idKeys = batchInsert(apis, "name");

        // Record operation logs if requested
        if (saveOperationLog) {
          operationLogCmd.addAll(API, apis, CREATED);
        }
        return idKeys;
      }
    }.execute();
  }

  /**
   * Updates existing API endpoints with new information.
   * 
   * <p>This method ensures data consistency by:</p>
   * <ul>
   *   <li>Validating that APIs exist before update</li>
   *   <li>Updating service information when service associations change</li>
   *   <li>Maintaining audit trails through operation logs</li>
   * </ul>
   * 
   * @param apis List of API entities to update
   * @param saveOperationLog Whether to record operation logs
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Api> apis, boolean saveOperationLog) {
    new BizTemplate<Void>() {
      List<Api> apisDb;
      List<Service> servicesDb;

      @Override
      protected void checkParams() {
        // Validate that APIs exist before update
        Set<Long> apiIds = apis.stream().map(Api::getId).collect(Collectors.toSet());
        apisDb = apiQuery.checkAndFind(apiIds, false);
        
        // Validate associated services exist
        Set<Long> serviceIds = apis.stream().filter(Objects::nonNull)
            .map(Api::getServiceId).collect(Collectors.toSet());
        if (isNotEmpty(serviceIds)) {
          servicesDb = serviceQuery.checkAndFind(serviceIds, false);
        }
      }

      @Override
      protected Void process() {
        // Update service information when service associations change
        updateServiceWhenChanged(apis, apisDb, servicesDb);

        // Save updated APIs with validation
        batchUpdateOrNotFound(apis);

        // Record operation logs if requested
        if (saveOperationLog) {
          operationLogCmd.addAll(API, apis, CREATED);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Replaces APIs by creating new ones or updating existing ones.
   * 
   * <p>This method handles both creation and update scenarios:</p>
   * <ul>
   *   <li>Creates new APIs for entities without IDs</li>
   *   <li>Updates existing APIs for entities with IDs</li>
   *   <li>Maintains data consistency across related entities</li>
   * </ul>
   * 
   * @param apis List of API entities to replace
   * @return List of API identifiers with names
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> replace(List<Api> apis) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      List<Api> replaceApis;
      List<Api> replaceApisDb;

      @Override
      protected void checkParams() {
        // Identify APIs that need to be updated (have existing IDs)
        replaceApis = apis.stream().filter(api -> nonNull(api.getId()))
            .collect(Collectors.toList());
        replaceApisDb = apiQuery.checkAndFind(replaceApis.stream().map(Api::getId)
            .collect(Collectors.toList()), false);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        // Create new APIs for entities without IDs
        List<Api> addApis = apis.stream().filter(api -> isNull(api.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addApis)) {
          idKeys.addAll(add(addApis, true));
        }

        // Update existing APIs
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

  /**
   * Deletes API endpoints and cleans up related data.
   * 
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Removing API associations from applications and functions</li>
   *   <li>Deleting API authority records</li>
   *   <li>Removing system token authorizations</li>
   *   <li>Recording deletion audit logs</li>
   * </ul>
   * 
   * @param apiIds Collection of API identifiers to delete
   */
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

        // Delete APIs from repository
        apiRepo.deleteByIdIn(apiIds);

        // Remove API associations from applications and functions
        deleteAppAndFuncApis(apiIds);

        // Delete API authority records
        apiAuthorityRepo.deleteByApiIdIn(apiIds);

        // Remove system token authorizations
        systemTokenCmd.deleteByApiIdIn(apiIds);

        // Record deletion audit logs
        operationLogCmd.addAll(API, apisDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Enables or disables API endpoints and updates related authorization data.
   * 
   * <p>Note: Enabling/disabling APIs can lead to authorization data consistency issues.
   * This implementation focuses on maintaining valid data for production environments
   * and considers API removal for later cleanup.</p>
   * 
   * @param apis List of APIs with updated enabled status
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(List<Api> apis) {
    new BizTemplate<Void>() {
      Set<Long> apiIds;
      List<Api> apisDb;

      @Override
      protected void checkParams() {
        // Validate that APIs exist before status update
        apiIds = apis.stream().map(Api::getId).collect(Collectors.toSet());
        apisDb = apiQuery.checkAndFind(apiIds, false);
      }

      @Override
      protected Void process() {
        // Update enabled/disabled status
        batchUpdate0(batchCopyPropertiesIgnoreNull(apis, apisDb));

        // Synchronize API enabled/disabled status to authority records
        List<ApiAuthority> authorities = apiAuthorityRepo.findByApiIdIn(apiIds);
        if (isNotEmpty(authorities)) {
          Map<Long, Api> apiMap = apis.stream().collect(Collectors.toMap(Api::getId, x -> x));
          for (ApiAuthority authority : authorities) {
            authority.setApiEnabled(apiMap.get(authority.getApiId()).getEnabled());
          }
          apiAuthorityRepo.saveAll(authorities);
        }

        // Note: System token authorization resource status update is handled separately
        // Alternative: Manual deletion of disabled APIs

        // Record status change audit logs
        operationLogCmd.addAll(
            API, apisDb.stream().filter(Api::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(
            API, apisDb.stream().filter(x -> !x.getEnabled()).toList(), DISABLED);
        return null;
      }
    }.execute();
  }

  /**
   * Synchronizes APIs from a specific service instance.
   * 
   * <p>This method parses Swagger documentation from the service instance
   * and saves the discovered APIs to the database.</p>
   * 
   * @param instance Service instance containing API information
   * @param serviceDb Service entity to associate with APIs
   */
  @Override
  public void syncServiceApi(ServiceInstance instance, Service serviceDb) {
    // Parse APIs from Swagger documentation
    List<Api> apis = parseApisFromSwagger(instance, serviceDb);

    // Save synchronized APIs to database
    saveSyncApis(serviceDb.getCode(), apis);
  }

  /**
   * Synchronizes APIs from multiple service instances discovered through service discovery.
   * 
   * <p>This method processes multiple services and their instances to discover
   * and synchronize API endpoints from Swagger documentation.</p>
   * 
   * @param instances List of discovered service instances
   * @param servicesDb List of service entities in database
   * @param discoveryServices List of service codes to process
   */
  @Override
  public void discoveryApiSync(List<ServiceInstance> instances, List<Service> servicesDb,
      List<String> discoveryServices) {
    Map<String, Service> serviceDbMap = servicesDb.stream()
        .filter(service -> discoveryServices.contains(service.getCode().toLowerCase()))
        .collect(Collectors.toMap(service -> service.getCode().toLowerCase(), service -> service));
    Map<String, ServiceInstance> instancesMap = instances.stream()
        .collect(Collectors.toMap(ServiceInstance::getServiceId, si -> si));
    
    for (Service serviceDb : serviceDbMap.values()) {
      // Parse APIs from Swagger documentation for each service
      List<Api> apis = parseApisFromSwagger(instancesMap.get(serviceDb.getCode()), serviceDb);
      ServiceInstance serviceInstance = instancesMap.get(serviceDb.getCode());
      if (isEmpty(apis) || isNull(serviceInstance)) {
        continue;
      }
      // Save synchronized APIs to database
      saveSyncApis(serviceDb.getCode(), apis);
    }
  }

  /**
   * Updates API service status based on service enable/disable state.
   * 
   * <p>This method propagates service status changes to associated APIs
   * to maintain consistency between services and their APIs.</p>
   * 
   * @param services List of services with updated status
   */
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

  /**
   * Saves synchronized APIs with intelligent update logic.
   * 
   * <p>This method implements a sophisticated synchronization strategy:</p>
   * <ul>
   *   <li>Adds new APIs that don't exist in database</li>
   *   <li>Updates existing APIs with new information</li>
   *   <li>Marks APIs as deleted if they no longer exist in Swagger</li>
   * </ul>
   * 
   * @param serviceCode Service code for API association
   * @param apis List of APIs from Swagger documentation
   */
  @Override
  public void saveSyncApis(String serviceCode, List<Api> apis) {
    if (isEmpty(apis)) {
      return;
    }
    List<Api> apisDb = apiRepo.findAllByServiceCodeIn(singletonList(serviceCode));
    List<Api> updateApisCopy = new ArrayList<>(apis);
    List<Api> deletedApisCopy = new ArrayList<>(apis);
    List<Api> deletedApisDbCopy = new ArrayList<>(apisDb);

    // If database is empty, add all APIs directly
    if (isEmpty(apisDb)) {
      add(apis, false);
      return;
    }

    // Update existing APIs in database
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

    // Add new APIs from Swagger documentation
    CoreUtils.removeAll(apis, updateApisCopy);
    if (isNotEmpty(apis)) {
      add(apis, false);
    }

    // Mark APIs as deleted if they no longer exist in Swagger
    CoreUtils.removeAll(deletedApisDbCopy, deletedApisCopy);
    if (isNotEmpty(deletedApisDbCopy)) {
      update(deletedApisDbCopy.stream().map(api -> api.setSwaggerDeleted(true))
          .collect(Collectors.toList()), false);
    }
  }

  /**
   * Removes API associations from applications and functions.
   * 
   * <p>This method ensures that when APIs are deleted, all references
   * to those APIs are properly cleaned up from applications and functions.</p>
   * 
   * @param apiIds Collection of API identifiers to remove
   */
  private void deleteAppAndFuncApis(Collection<Long> apiIds) {
    List<App> apps = appRepo.findAll();
    if (isNotEmpty(apps)) {
      List<App> updateApiApps = new ArrayList<>();
      for (App app : apps) {
        if (isNotEmpty(app.getApiIds()) && app.getApiIds().removeAll(apiIds)) {
          updateApiApps.add(app);
        }
        // Remove API associations from functions
        updateFuncApis(app, apiIds);
      }
      if (isNotEmpty(updateApiApps)) {
        appRepo.saveAll(updateApiApps);
      }
    }
  }

  /**
   * Updates function API associations when APIs are deleted.
   * 
   * <p>This method removes deleted API references from application functions
   * to maintain data consistency.</p>
   * 
   * @param app Application entity
   * @param apiIds Collection of API identifiers to remove
   */
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

  /**
   * Parses APIs from Swagger documentation of a service instance.
   * 
   * <p>This method fetches Swagger documentation from multiple endpoints
   * and parses API information for different API types (API, PUB_API, OPEN_API_2P).</p>
   * 
   * @param serviceInstance Service instance to parse APIs from
   * @param serviceDb Service entity for API association
   * @return List of parsed API entities
   * @throws BizException if Swagger parsing fails
   */
  @Override
  public List<Api> parseApisFromSwagger(ServiceInstance serviceInstance, Service serviceDb) {
    List<Api> apis = new ArrayList<>();
    try {
      HttpUrlConnectionSender httpSender = new HttpUrlConnectionSender();
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender, AuthConstant.SWAGGER_API_URL,
          ApiType.API);
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender,
          AuthConstant.SWAGGER_PUB_API_URL, ApiType.PUB_API);
      // Note: Inner API parsing is commented out
      // parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender,
      //     AuthConstant.SWAGGER_INNER_API_URL, ApiType.DOOR_API);
      parseSwaggerDocs(apis, serviceDb, serviceInstance, httpSender,
          AuthConstant.SWAGGER_OPEN_API_TO_PRIVATE_URL, ApiType.OPEN_API_2P);
    } catch (Throwable e) {
      log.error("Parse swagger exception", e);
      throw BizException.of(API_SWAGGER_PARSE_ERROR_CODE, API_SWAGGER_PARSE_ERROR,
          new String[]{serviceDb.getCode()});
    }
    return apis;
  }

  /**
   * Updates service information when service associations change.
   * 
   * <p>This method ensures that when an API's service association changes,
   * the service code and name are properly updated.</p>
   * 
   * @param apis List of APIs to update
   * @param apisDb Existing APIs from database
   * @param servicesDb Services from database
   */
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
