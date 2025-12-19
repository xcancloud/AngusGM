package cloud.xcan.angus.core.gm.application.cmd.app.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkToUserRequired;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.app.open.AppOpenRepo;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppCmd;
import cloud.xcan.angus.core.gm.application.cmd.authority.ApiAuthorityCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthoritySource;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of application command operations for managing application lifecycle.
 *
 * <p>This class provides comprehensive functionality for application management including:</p>
 * <ul>
 *   <li>Creating, updating, and deleting applications</li>
 *   <li>Managing application tags and authorities</li>
 *   <li>Enabling/disabling applications with authorization synchronization</li>
 *   <li>Handling site-specific application updates</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 *
 * <p>The implementation ensures data consistency across related entities such as functions,
 * policies, and authorization records when applications are modified or deleted.</p>
 */
@org.springframework.stereotype.Service
public class AppCmdImpl extends CommCmd<App, Long> implements AppCmd {

  @Resource
  private AppRepo appRepo;
  @Resource
  private AppQuery appQuery;
  @Resource
  private AppFuncRepo appFuncRepo;
  @Resource
  private AppOpenRepo appOpenRepo;
  @Resource
  private AuthClientQuery clientQuery;
  @Resource
  private AuthPolicyCmd authPolicyCmd;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private ApiQuery apiQuery;
  @Resource
  private WebTagTargetCmd webTagTargetCmd;
  @Resource
  private ApiAuthorityCmd apiAuthorityCmd;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates a new application with associated tags and authorities.
   *
   * <p>This method performs comprehensive application creation including:</p>
   * <ul>
   *   <li>Validating application uniqueness and dependencies</li>
   *   <li>Creating application tags for categorization</li>
   *   <li>Setting up API authorities for access control</li>
   *   <li>Recording creation audit logs</li>
   * </ul>
   *
   * @param app Application entity to create
   * @return Application identifier with associated data
   */
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public IdKey<Long, Object> add(App app) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Validate application code and version uniqueness
        appQuery.checkUniqueCodeAndVersion(app);
        // Validate that associated OAuth2 client exists
        clientQuery.checkAndFind(app.getClientId());
        // Validate that associated APIs exist
        apiQuery.checkAndFind(app.getApiIds(), true);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Save application to database
        IdKey<Long, Object> idKeys = insert(app);

        // Create application tags for categorization
        webTagTargetCmd.tag(WebTagTargetType.APP, app.getId(), app.getTagIds());

        // Set up API authorities for access control
        apiAuthorityCmd.saveAppApiAuthority(app);

        // Record creation audit log
        operationLogCmd.add(APP, app, CREATED);
        return idKeys;
      }
    }.execute();
  }

  /**
   * Updates an existing application with new information.
   *
   * <p>This method ensures data consistency by:</p>
   * <ul>
   *   <li>Validating application uniqueness and dependencies</li>
   *   <li>Updating application tags</li>
   *   <li>Replacing API authorities</li>
   *   <li>Recording update audit logs</li>
   * </ul>
   *
   * @param app Application entity with updated information
   */
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void update(App app) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Validate application code and version uniqueness
        appQuery.checkUniqueCodeAndVersion(app);
        // Validate that associated OAuth2 client exists
        clientQuery.checkAndFind(app.getClientId(), true);
        // Validate that associated APIs exist
        apiQuery.checkAndFind(app.getApiIds(), true);
      }

      @Override
      protected Void process() {
        // Update application in database
        App appDb = updateOrNotFound(app);

        // Update application tags
        webTagTargetCmd.tag(WebTagTargetType.APP, app.getId(), app.getTagIds());

        // Replace application API authorities
        apiAuthorityCmd.replaceAppApiAuthority(appDb);

        // Record update audit log
        operationLogCmd.add(APP, app, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces an application by creating new or updating existing.
   *
   * <p>This method handles both creation and update scenarios:</p>
   * <ul>
   *   <li>Creates new application if no ID is provided</li>
   *   <li>Updates existing application if ID is provided</li>
   *   <li>Maintains audit trails for all operations</li>
   * </ul>
   *
   * @param app Application entity to replace
   * @return Application identifier with associated data
   */
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public IdKey<Long, Object> replace(App app) {
    return new BizTemplate<IdKey<Long, Object>>() {
      App replaceAppDb;

      @Override
      protected void checkParams() {
        // Validate that application exists if updating
        if (nonNull(app.getId())) {
          replaceAppDb = appQuery.checkAndFind(app.getId(), false);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(app.getId())) {
          return add(app);
        }

        // Update existing application
        update(copyPropertiesIgnoreTenantAuditing(app, replaceAppDb, "enabled"));

        // Record update audit log
        operationLogCmd.add(APP, app, UPDATED);

        return IdKey.of(replaceAppDb.getId(), replaceAppDb.getName());
      }
    }.execute();
  }

  /**
   * Updates application site-specific information.
   *
   * <p>This method handles site-specific updates with additional security checks:</p>
   * <ul>
   *   <li>Validates user permissions for cloud service editions</li>
   *   <li>Updates only site-specific fields (name, icon, URL)</li>
   *   <li>Maintains audit trails for site changes</li>
   * </ul>
   *
   * @param app Application entity with site-specific updates
   */
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void siteUpdate(App app) {
    new BizTemplate<Void>(false) {
      App appDb;

      @Override
      protected void checkParams() {
        // Cloud service version can only be modified by operating users
        if (isCloudServiceEdition()) {
          checkToUserRequired();
        }
        // Validate that application exists
        appDb = appQuery.checkAndFind(app.getId(), false);
      }

      @Override
      protected Void process() {
        // Update application site information (name, icon, URL)
        appRepo.save(copyPropertiesIgnoreNull(app, appDb));

        // Record site update audit log
        operationLogCmd.add(APP, app, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Deletes applications and cleans up related data.
   *
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Removing application functions</li>
   *   <li>Deleting associated authorization policies</li>
   *   <li>Cleaning up application open records</li>
   *   <li>Removing tags and API authorities</li>
   *   <li>Recording deletion audit logs</li>
   * </ul>
   *
   * @param ids Set of application identifiers to delete
   */
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      List<App> appsDb;

      @Override
      protected void checkParams() {
        appsDb = appQuery.checkAndFind(ids, false);
      }

      @Override
      protected Void process() {
        // Delete applications from repository
        appRepo.deleteByIdIn(ids);
        // Remove associated application functions
        appFuncRepo.deleteByAppIdIn(ids);

        // Delete associated authorization policies
        Set<Long> appPolicyIds = authPolicyQuery.findByAppIdIn(ids).stream()
            .map(AuthPolicy::getAppId).collect(Collectors.toSet());
        if (isNotEmpty(appPolicyIds)) {
          authPolicyCmd.delete(appPolicyIds);
        }

        // Clean up application open records
        appOpenRepo.deleteByAppIdIn(ids);
        // Remove application tags
        webTagTargetCmd.delete(ids);
        // Delete API authorities associated with applications
        apiAuthorityCmd.deleteBySource(ids, ApiAuthoritySource.APP);

        // Record deletion audit logs
        operationLogCmd.addAll(APP, appsDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Enables or disables applications and synchronizes authorization status.
   *
   * <p>This method ensures authorization consistency by:</p>
   * <ul>
   *   <li>Updating application enabled status</li>
   *   <li>Synchronizing API authority status</li>
   *   <li>Recording status change audit logs</li>
   * </ul>
   *
   * @param apps List of applications with updated enabled status
   */
  @Override
  public void enabled(List<App> apps) {
    new BizTemplate<Void>() {
      List<App> appsDb;
      Set<Long> ids;

      @Override
      protected void checkParams() {
        // Validate that applications exist
        ids = apps.stream().map(App::getId).collect(Collectors.toSet());
        appsDb = appQuery.checkAndFind(ids, false);
        // Update enabled status for applications in database
        Map<Long, App> appsMap = apps.stream().collect(Collectors.toMap(App::getId, x -> x));
        for (App appDb : appsDb) {
          appDb.setEnabled(appsMap.get(appDb.getId()).getEnabled());
        }
      }

      @Override
      protected Void process() {
        // Update application enabled status
        appRepo.saveAll(appsDb);

        // Synchronize API authority status
        apiAuthorityCmd.updateAppAuthorityStatus(apps, ids);

        // Record status change audit logs
        operationLogCmd.addAll(APP, appsDb.stream().filter(App::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(APP, appsDb.stream().filter(x -> !x.getEnabled()).toList(),
            DISABLED);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<App, Long> getRepository() {
    return this.appRepo;
  }
}
