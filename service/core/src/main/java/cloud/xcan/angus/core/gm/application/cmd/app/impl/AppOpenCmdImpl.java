package cloud.xcan.angus.core.gm.application.cmd.app.impl;


import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.APP_OPEN;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.APP_OPEN_RENEW;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOriginalOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasOriginalOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.api.commonlink.app.open.AppOpenRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOpenCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of application open command operations for managing application access.
 * 
 * <p>This class provides comprehensive functionality for application access management including:</p>
 * <ul>
 *   <li>Opening applications for tenants with authorization setup</li>
 *   <li>Renewing application access after expiration</li>
 *   <li>Canceling application access and cleaning up authorizations</li>
 *   <li>Managing application expiration and cleanup</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>Note: This implementation handles internal non-multi-tenant tables and privatization.
 * Base applications and operation applications are excluded, and base applications
 * are initialized during tenant signup.</p>
 * 
 * @author XiaoLong Liu
 */
@Slf4j
@Biz
public class AppOpenCmdImpl extends CommCmd<AppOpen, Long> implements AppOpenCmd {

  @Resource
  private AppOpenRepo appOpenRepo;
  @Resource
  private AppOpenQuery appOpenQuery;
  @Resource
  private AuthPolicyTenantCmd authPolicyTenantCmd;
  @Resource
  private AppQuery appQuery;
  @Resource
  private TenantQuery tenantQuery;
  @Resource
  private UserQuery userQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Opens an application for a tenant with authorization setup.
   * 
   * <p>This method performs comprehensive application opening including:</p>
   * <ul>
   *   <li>Validating application, tenant, and user existence</li>
   *   <li>Setting up application access records</li>
   *   <li>Initializing tenant authorization policies</li>
   *   <li>Recording opening audit logs</li>
   * </ul>
   * 
   * @param appOpen Application open entity with access details
   * @param saveOperationLog Whether to record operation logs
   * @return Application open identifier with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> open(AppOpen appOpen, boolean saveOperationLog) {
    return new BizTemplate<IdKey<Long, Object>>(false) {
      App appDb;
      Tenant tenantDb;
      User userDb;

      @Override
      protected void checkParams() {
        // Validate that application exists and is enabled
        appDb = appQuery.checkAndFind(appOpen.getEditionType(), appOpen.getAppCode(),
            appOpen.getVersion(), true);
        appOpen.setAppId(appDb.getId());

        // Validate that tenant exists
        tenantDb = tenantQuery.checkAndFind(appOpen.getTenantId());

        // Validate that user exists if specified
        if (nonNull(appOpen.getUserId())) {
          userDb = userQuery.checkAndFind(appOpen.getUserId());
        }

        // Note: Only cloud applications are allowed, but base applications and operation applications are not
        // ProtocolAssert.assertTrue(appDb.isCloudApp(), "Only cloud applications are allowed");

        // Validate application ID, code, and version consistency
        assertTrue(appOpen.getAppId().equals(appDb.getId())
            && appOpen.getEditionType().equals(appDb.getEditionType())
            && appOpen.getAppCode().equals(appDb.getCode())
            && appOpen.getVersion().equals(appDb.getVersion()), String
            .format("appId[%s],code[%s],version[%s] is inconsistent", appOpen.getAppId(),
                appOpen.getAppCode(), appOpen.getVersion()));

        // Note: Repeated application opening is allowed by external jobs
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Handle reopening when modification expiration time already exists
        AppOpen appOpenDb = appOpenRepo.findByTenantIdAndEditionTypeAndAppCodeAndVersion(
            appOpen.getTenantId(), appOpen.getEditionType(), appOpen.getAppCode(),
            appOpen.getVersion());
        if (nonNull(appOpenDb)) {
          appOpenDb.setExpirationDate(appOpenDb.getExpirationDate());
          appOpenRepo.save(appOpenDb);
          return new IdKey<>(appOpenDb.getId(), null);
        }

        // Save application open record for first-time access
        appOpen.setOpClientOpen(isOpClient()).setClientId(appDb.getClientId());
        appOpen.setAppType(appDb.getType()).setExpirationDeleted(false);
        appOpen.setCreatedDate(LocalDateTime.now());
        IdKey<Long, Object> idKey = insert(appOpen);

        // Initialize tenant default (_GUEST or _USER) and administrator (_ADMIN) authorization policies
        authPolicyTenantCmd.intAppAndPolicyByTenantAndApp(appOpen.getTenantId(), appDb);

        // Record opening audit log if requested
        if (saveOperationLog){
          if (!isUserAction()) {
            PrincipalContext.get().setClientId(appDb.getClientId())
                .setTenantId(tenantDb.getId()).setTenantName(tenantDb.getName())
                .setUserId(nonNull(userDb) ? userDb.getId() : -1L)
                .setFullName(nonNull(userDb) ? userDb.getFullName() : "System");
            operationLogCmd.add(APP, appDb, APP_OPEN);
          }
        }
        return idKey;
      }
    }.execute();
  }

  /**
   * Renews application access after expiration.
   * 
   * <p>This method handles application renewal by updating expiration dates
   * and maintaining authorization policies.</p>
   * 
   * @param appOpen Application open entity with renewal details
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void renew(AppOpen appOpen) {
    new BizTemplate<Void>(false) {
      App appDb;
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Validate that application exists
        appDb = appQuery.checkAndFind(appOpen.getEditionType(), appOpen.getAppCode(),
            appOpen.getVersion(), true);
        appOpen.setAppId(appDb.getId());

        // Validate that tenant exists
        tenantDb = tenantQuery.checkAndFind(appOpen.getTenantId());

        // Validate that user exists if specified
        if (nonNull(appOpen.getUserId())) {
          userQuery.checkAndFind(appOpen.getUserId());
        }

        // Note: Expiration validation is commented out to allow flexible renewal dates
        // ProtocolAssert.assertResourceNotFound(!appOpensDb.getExpirationDeleted(), String
        //    .format("Tenant %s open app %s is expired", getOriginalOptTenantId(), appOpen.getAppId()));
      }

      @Override
      protected Void process() {
        try {
          AppOpen appOpensDb = appOpenQuery.checkAndFind(appOpen.getAppId(),
              appOpen.getTenantId(), false);
          appOpensDb.setExpirationDate(appOpen.getExpirationDate());
          appOpenRepo.save(appOpensDb);

          // Record renewal audit log
          if (!isUserAction()) {
            PrincipalContext.get().setClientId(appDb.getClientId())
                .setTenantId(tenantDb.getId()).setTenantName(tenantDb.getName())
                .setUserId(-1L).setFullName("System");
            operationLogCmd.add(APP, appDb, APP_OPEN_RENEW);
          }
        } catch (Exception e) {
          if (e instanceof ResourceNotFound) {
            // If application open record doesn't exist, create new one
            open(appOpen, false);
          } else {
            throw e;
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * Cancels application access and cleans up authorization policies.
   * 
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Removing application open records</li>
   *   <li>Canceling tenant authorization policies</li>
   *   <li>Recording cancellation audit logs</li>
   * </ul>
   * 
   * @param appId Application identifier to cancel
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancel(Long appId) {
    new BizTemplate<Void>(false) {
      App appDb;
      AppOpen appOpensDb;

      @Override
      protected void checkParams() {
        // Validate that application exists
        appDb = appQuery.checkAndFind(appId, false);
        // Validate that tenant ID is required for cancellation
        assertTrue(hasOriginalOptTenantId(),
            "Open parameter optTenantId is required");
        // Validate that application open record exists
        appOpensDb = appOpenQuery.checkAndFind(appId, getOriginalOptTenantId(), true);
      }

      @Override
      protected Void process() {
        // Delete application open record
        // Note: @CheckAppNotExpired will trigger verification of non-opened applications
        appOpenRepo.delete(appOpensDb);

        // Cancel opened authorization policies of tenant users and administrator
        // Note: Authorization policies are retained
        authPolicyTenantCmd.appOpenPolicyCancel(getOriginalOptTenantId(), appId);

        log.info("Cancel tenant {} open application {} ", appId, appId);

        // Record cancellation audit log
        if (!isUserAction()) {
          Tenant tenantDb = tenantQuery.checkAndFind(appOpensDb.getTenantId());
          PrincipalContext.get().setClientId(appDb.getClientId())
              .setTenantId(tenantDb.getId()).setTenantName(tenantDb.getName())
              .setUserId(-1L).setFullName("System");
          operationLogCmd.add(APP, appDb, APP_OPEN_RENEW);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Updates expired application records.
   * 
   * <p>This method marks application open records as expired based on
   * current date for cleanup purposes.</p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void expiredUpdate() {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        appOpenRepo.updateExpiredByBeforeDate(LocalDateTime.now());
        return null;
      }
    }.execute();
  }

  /**
   * Opens an application without validation checks.
   * 
   * <p>This method provides a simplified application opening process
   * for internal use without comprehensive validation.</p>
   * 
   * @param appOpen Application open entity
   * @param appDb Application entity
   */
  @Override
  public void open0(AppOpen appOpen, App appDb) {
    // Save application open record with basic information
    appOpen.setOpClientOpen(isOpClient()).setClientId(appDb.getClientId());
    appOpen.setAppType(appDb.getType()).setExpirationDeleted(false);
    appOpen.setCreatedDate(LocalDateTime.now());
    insert0(appOpen);
  }

  /**
   * Opens multiple applications without validation checks.
   * 
   * <p>This method provides batch application opening for internal use
   * without comprehensive validation.</p>
   * 
   * @param appOpens List of application open entities
   * @param appDb Application entity
   */
  @Override
  public void open0(List<AppOpen> appOpens, App appDb) {
    // Save multiple application open records with basic information
    for (AppOpen appOpen : appOpens) {
      appOpen.setOpClientOpen(isOpClient()).setClientId(appDb.getClientId());
      appOpen.setAppType(appDb.getType()).setExpirationDeleted(false);
      appOpen.setCreatedDate(LocalDateTime.now());
    }
    batchInsert0(appOpens);
  }

  @Override
  protected BaseRepository<AppOpen, Long> getRepository() {
    return appOpenRepo;
  }
}
