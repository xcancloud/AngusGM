package cloud.xcan.angus.core.gm.application.cmd.app.impl;

import static cloud.xcan.angus.core.gm.application.converter.ApiAuthorityConverter.toFuncAuthority;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP_FUNC;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppFuncCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthority;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthorityRepo;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthoritySource;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of application function command operations for managing application functions.
 * 
 * <p>This class provides comprehensive functionality for application function management including:</p>
 * <ul>
 *   <li>Creating, updating, and deleting application functions</li>
 *   <li>Managing function hierarchies and parent-child relationships</li>
 *   <li>Handling function tags and API authorities</li>
 *   <li>Enabling/disabling functions with cascading effects</li>
 *   <li>Importing functions from external sources</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures data consistency across related entities such as APIs,
 * authorities, and tags when functions are modified or deleted.</p>
 */
@Biz
public class AppFuncCmdImpl extends CommCmd<AppFunc, Long> implements AppFuncCmd {

  @Resource
  private AppFuncRepo appFuncRepo;
  @Resource
  private AppFuncQuery appFuncQuery;
  @Resource
  private AppQuery appQuery;
  @Resource
  private ApiQuery apiQuery;
  @Resource
  private WebTagTargetCmd webTagTargetCmd;
  @Resource
  private ApiAuthorityRepo apiAuthorityRepo;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates new application functions with associated tags and authorities.
   * 
   * <p>This method performs comprehensive function creation including:</p>
   * <ul>
   *   <li>Validating function hierarchy and uniqueness</li>
   *   <li>Setting up function tags for categorization</li>
   *   <li>Creating API authorities for access control</li>
   *   <li>Recording creation audit logs</li>
   * </ul>
   * 
   * @param appId Application identifier
   * @param appFunc List of application function entities to create
   * @return List of created function identifiers with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(Long appId, List<AppFunc> appFunc) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      App appDb;

      @Override
      protected void checkParams() {
        // Validate that application exists
        appDb = appQuery.checkAndFind(appId, true);
        // Validate that parent functions exist under the same application
        appFuncQuery.checkAndFind(appId, appFunc.stream().filter(AppFunc::hasParent)
            .map(AppFunc::getPid).collect(Collectors.toSet()), true);
        // Validate function code uniqueness under the same application
        appFuncQuery.checkAddCodeExist(appId, appFunc);
        // Note: API validation is handled in saveFuncApiAuthority()
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Set application information for functions
        setFuncAppInfo(appDb, appFunc);
        // Add functions with batch insertion
        List<IdKey<Long, Object>> idKeys = batchInsert(appFunc, "code");

        // Create function tags for categorization
        appFunc.forEach(func -> {
          webTagTargetCmd.tag(func.getType().toTagTargetType(), func.getId(), func.getTagIds());
        });

        // Set up API authorities for access control
        saveFuncApiAuthority(appDb, appFunc);

        // Record creation audit logs
        operationLogCmd.addAll(APP_FUNC, appFunc, CREATED);
        return idKeys;
      }
    }.execute();
  }

  /**
   * Updates existing application functions with new information.
   * 
   * <p>This method ensures data consistency by:</p>
   * <ul>
   *   <li>Validating function hierarchy and uniqueness</li>
   *   <li>Updating function tags</li>
   *   <li>Replacing API authorities</li>
   *   <li>Recording update audit logs</li>
   * </ul>
   * 
   * @param appId Application identifier
   * @param appFunc List of application function entities to update
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      App appDb;

      @Override
      protected void checkParams() {
        // Validate that application exists
        appDb = appQuery.checkAndFind(appId, true);
        // Validate that application functions exist
        appFuncQuery.checkAndFind(appId, appFunc.stream().map(AppFunc::getId)
            .collect(Collectors.toSet()), false);
        // Validate that parent functions exist under the same application
        appFuncQuery.checkAndFind(appId, appFunc.stream().filter(AppFunc::hasParent)
            .map(AppFunc::getPid).collect(Collectors.toSet()), false);
        // Validate function code uniqueness under the same application
        appFuncQuery.checkUpdateCodeExist(appId, appFunc);
        // Note: API validation is handled in saveFuncApiAuthority()
      }

      @Override
      protected Void process() {
        // Update functions in database
        List<AppFunc> appFuncDb = batchUpdateOrNotFound(appFunc);

        // Update function tags
        Map<Long, AppFunc> appFuncsMap = appFuncDb.stream()
            .collect(Collectors.toMap(AppFunc::getId, x -> x));
        appFunc.forEach(func -> {
          webTagTargetCmd.tag(appFuncsMap.get(func.getId()).getType().toTagTargetType(),
              func.getId(), func.getTagIds());
        });

        // Replace function API authorities
        replaceFuncApiAuthority(appDb, appFunc);

        // Record update audit logs
        operationLogCmd.addAll(APP_FUNC, appFuncDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces application functions by creating new or updating existing.
   * 
   * <p>This method handles both creation and update scenarios:</p>
   * <ul>
   *   <li>Creates new functions for entities without IDs</li>
   *   <li>Updates existing functions for entities with IDs</li>
   *   <li>Maintains data consistency across related entities</li>
   * </ul>
   * 
   * @param appId Application identifier
   * @param appFunc List of application function entities to replace
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      List<AppFunc> replaceFunc;
      List<AppFunc> replaceFuncDb;

      @Override
      protected void checkParams() {
        // Identify functions that need to be updated (have existing IDs)
        replaceFunc = appFunc.stream().filter(func -> Objects.nonNull(func.getId()))
            .collect(Collectors.toList());
        replaceFuncDb = appFuncQuery.checkAndFind(appId, replaceFunc.stream()
            .map(AppFunc::getId).collect(Collectors.toSet()), false);
      }

      @Override
      protected Void process() {
        // Create new functions for entities without IDs
        List<AppFunc> addFunc = appFunc.stream().filter(app -> Objects.isNull(app.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addFunc)) {
          add(appId, addFunc);
        }

        // Update existing functions
        if (isNotEmpty(replaceFunc)) {
          Map<Long, AppFunc> appFuncMap = replaceFuncDb.stream()
              .collect(Collectors.toMap(AppFunc::getId, x -> x));
          // Do not replace enabled status and type
          update(appId, replaceFunc.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, appFuncMap.get(x.getId()),
                  "enabled", "type"))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  /**
   * Deletes application functions and cleans up related data.
   * 
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Removing function tags</li>
   *   <li>Deleting API authorities</li>
   *   <li>Handling cascading deletion of child functions</li>
   *   <li>Recording deletion audit logs</li>
   * </ul>
   * 
   * @param appId Application identifier
   * @param funcIds Set of function identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long appId, HashSet<Long> funcIds) {
    new BizTemplate<Void>() {
      List<AppFunc> appFuncDb;

      @Override
      protected void checkParams() {
        // Validate that functions exist
        appFuncDb = appFuncQuery.checkAndFind(appId, funcIds, false);
      }

      @Override
      protected Void process() {
        // Get all function and sub-function IDs for cascading deletion
        Set<Long> existedFuncAndSubIds = appFuncQuery.findFuncAndSubIds(appId, funcIds);
        if (isNotEmpty(existedFuncAndSubIds)) {
          // Delete functions from repository
          appFuncRepo.deleteByAppIdAndIdIn(appId, existedFuncAndSubIds);
          // Remove function tags
          webTagTargetCmd.delete(existedFuncAndSubIds);
          // Delete API authorities associated with functions
          apiAuthorityRepo.deleteBySourceIdInAndSource(existedFuncAndSubIds,
              ApiAuthoritySource.APP_FUNC.getValue());
        }

        // Record deletion audit logs
        operationLogCmd.addAll(APP_FUNC, appFuncDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Enables or disables application functions with cascading effects.
   * 
   * <p>This method ensures authorization consistency by:</p>
   * <ul>
   *   <li>Updating function enabled status</li>
   *   <li>Cascading disable child functions when parent is disabled</li>
   *   <li>Synchronizing API authority status</li>
   *   <li>Recording status change audit logs</li>
   * </ul>
   * 
   * @param appId Application identifier
   * @param appFunc List of application functions with updated enabled status
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      List<AppFunc> appFuncDb;
      Set<Long> ids;

      @Override
      protected void checkParams() {
        // Validate that functions exist
        ids = appFunc.stream().map(AppFunc::getId).collect(Collectors.toSet());
        appFuncDb = appFuncQuery.checkAndFind(appId, ids, false);
      }

      @Override
      protected Void process() {
        // Cascading disable child functions when parent function is disabled
        Set<Long> disabledAppFuncIds = appFunc.stream().filter(x -> !x.getEnabled())
            .map(AppFunc::getId).collect(Collectors.toSet());
        List<AppFunc> allSubs = null;
        if (isNotEmpty(disabledAppFuncIds)) {
          allSubs = appFuncQuery.findSub(appId, disabledAppFuncIds);
          if (isNotEmpty(allSubs)) {
            batchUpdate0(allSubs.stream().map(x -> x.setEnabled(false))
                .collect(Collectors.toList()));
          }
        }

        // Update function authority to enabled
        Set<Long> enabledAppFuncIds = appFunc.stream().filter(AppFunc::getEnabled)
            .map(AppFunc::getId).collect(Collectors.toSet());
        if (isNotEmpty(enabledAppFuncIds)) {
          updateAppFuncAuthorityStatus(enabledAppFuncIds, true);
        }

        // Update function and sub-function authority to disabled
        if (isNotEmpty(disabledAppFuncIds)) {
          if (isNotEmpty(allSubs)) {
            disabledAppFuncIds.addAll(allSubs.stream().map(AppFunc::getId).toList());
          }
          updateAppFuncAuthorityStatus(disabledAppFuncIds, false);
        }

        // Update function enabled/disabled status
        batchUpdateOrNotFound(appFunc);

        // Record status change audit logs
        operationLogCmd.addAll(APP_FUNC,
            appFuncDb.stream().filter(AppFunc::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(APP_FUNC,
            appFuncDb.stream().filter(x -> !x.getEnabled()).toList(), DISABLED);
        return null;
      }
    }.execute();
  }

  /**
   * Imports application functions from external sources.
   * 
   * <p>This method replaces existing functions with imported ones,
   * effectively performing a bulk import operation.</p>
   * 
   * @param appId Application identifier
   * @param appFunc List of application functions to import
   */
  @Override
  public void imports(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        replace(appId, appFunc);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces function API authorities by deleting existing and creating new ones.
   * 
   * <p>This method ensures that API authorities are properly synchronized
   * when function API associations change.</p>
   * 
   * @param app Application entity
   * @param appFunc List of application functions
   */
  private void replaceFuncApiAuthority(App app, List<AppFunc> appFunc) {
    if (isNotEmpty(appFunc) && appFunc.stream().anyMatch(x -> isNotEmpty(x.getApiIds()))) {
      deleteFuncApiAuthority(appFunc);
      saveFuncApiAuthority(app, appFunc);
    }
  }

  /**
   * Deletes API authorities associated with functions.
   * 
   * <p>This method removes all API authority records for the specified functions
   * to prepare for new authority creation.</p>
   * 
   * @param appFunc List of application functions
   */
  private void deleteFuncApiAuthority(List<AppFunc> appFunc) {
    Set<Long> funcIds = appFunc.stream().map(AppFunc::getId).collect(Collectors.toSet());
    if (isNotEmpty(funcIds)) {
      apiAuthorityRepo.deleteBySourceIdInAndSource(funcIds, ApiAuthoritySource.APP_FUNC.getValue());
    }
  }

  /**
   * Creates API authorities for functions and their associated APIs.
   * 
   * <p>This method validates API associations and creates authority records
   * for proper access control.</p>
   * 
   * @param app Application entity
   * @param appFunc List of application functions
   */
  private void saveFuncApiAuthority(App app, List<AppFunc> appFunc) {
    List<ApiAuthority> authorities = new ArrayList<>();
    for (AppFunc func : appFunc) {
      if (isNotEmpty(func.getApiIds())) {
        // Validate that associated APIs exist
        List<Api> apisDb = apiQuery.checkAndFind(func.getApiIds(), true);
        if (isNotEmpty(apisDb)) {
          for (Api api : apisDb) {
            authorities.add(toFuncAuthority(app, func, api, uidGenerator.getUID()));
          }
        }
      }
    }
    if (isNotEmpty(authorities)) {
      apiAuthorityRepo.batchInsert(authorities);
    }
  }

  /**
   * Sets application information for functions.
   * 
   * <p>This method ensures that functions inherit the client ID and tenant ID
   * from their parent application.</p>
   * 
   * @param appDb Application entity
   * @param appFunc List of application functions
   */
  private void setFuncAppInfo(App appDb, List<AppFunc> appFunc) {
    appFunc.forEach(func -> {
      func.setClientId(appDb.getClientId()).setTenantId(appDb.getTenantId());
    });
  }

  /**
   * Updates function API authority status.
   * 
   * <p>This method synchronizes the enabled status of functions with their
   * associated API authorities.</p>
   * 
   * @param ids Collection of function identifiers
   * @param enabled Whether functions should be enabled
   */
  private void updateAppFuncAuthorityStatus(Collection<Long> ids, boolean enabled) {
    List<ApiAuthority> authorities = apiAuthorityRepo.findByAppIdIn(ids);
    if (isNotEmpty(authorities)) {
      for (ApiAuthority authority : authorities) {
        authority.setAppEnabled(enabled);
      }
      apiAuthorityRepo.saveAll(authorities);
    }
  }

  @Override
  protected BaseRepository<AppFunc, Long> getRepository() {
    return this.appFuncRepo;
  }
}
