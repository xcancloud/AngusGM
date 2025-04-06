package cloud.xcan.angus.core.gm.application.cmd.app.impl;


import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOriginalOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasOriginalOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.api.commonlink.app.open.AppOpenRepo;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOpenCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Internal non multi tenant table and privatization is also needed.
 * <p>
 * Base applications and operation applications should not be included, and the base application
 * will be initialized when the tenant signup.
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
  private TenantRepo tenantRepo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> open(AppOpen appOpen) {
    return new BizTemplate<IdKey<Long, Object>>(false) {
      App appDb;

      @Override
      protected void checkParams() {
        // Check the application existed and enabled
        appDb = appQuery.checkAndFind(appOpen.getEditionType(), appOpen.getAppCode(),
            appOpen.getVersion(), true);
        appOpen.setAppId(appDb.getId());

        // Check the tenant existed
        tenantRepo.findById(appOpen.getTenantId())
            .orElseThrow(() -> ResourceNotFound.of(appOpen.getTenantId(), "Tenant"));

        // Check only cloud applications are allowed, but base applications and operation applications are not.
        // ProtocolAssert.assertTrue(appDb.isCloudApp(), "Only cloud applications are allowed");

        // Check appId,code,version is consistent
        assertTrue(appOpen.getAppId().equals(appDb.getId())
            && appOpen.getEditionType().equals(appDb.getEditionType())
            && appOpen.getAppCode().equals(appDb.getCode())
            && appOpen.getVersion().equals(appDb.getVersion()), String
            .format("appId[%s],code[%s],version[%s] is inconsistent", appOpen.getAppId(),
                appOpen.getAppCode(), appOpen.getVersion()));

        // NOOP:: Check for repeated application opened and exclude expired -> Allow repeated calls by out job
      }

      @Override
      protected IdKey<Long, Object> process() {
        // If the modification expiration time already existed when reopen
        AppOpen appOpenDb = appOpenRepo.findByTenantIdAndEditionTypeAndAppCodeAndVersion(
            appOpen.getTenantId(), appOpen.getEditionType(), appOpen.getAppCode(),
            appOpen.getVersion());
        if (Objects.nonNull(appOpenDb)) {
          appOpenDb.setExpirationDate(appOpenDb.getExpirationDate());
          appOpenRepo.save(appOpenDb);
          return new IdKey<>(appOpenDb.getId(), null);
        }

        // Save application open when first time
        appOpen.setOpClientOpen(isOpClient()).setClientId(appDb.getClientId());
        appOpen.setAppType(appDb.getType()).setExpirationDeleted(false);
        appOpen.setCreatedDate(LocalDateTime.now());
        IdKey<Long, Object> idKey = insert(appOpen);

        // Initialize tenant default(_GUEST or _USER) and administrator(_ADMIN) authorization policies
        authPolicyTenantCmd.intAppAndPolicyByTenantAndApp(appOpen.getTenantId(), appDb);
        return idKey;
      }
    }.execute();
  }

  /**
   * Reopen after expiration.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void renew(AppOpen appOpen) {
    new BizTemplate<Void>(false) {

      @Override
      protected void checkParams() {
        // Check the application existed
        App appDb = appQuery.checkAndFind(appOpen.getEditionType(), appOpen.getAppCode(),
            appOpen.getVersion(), true);
        appOpen.setAppId(appDb.getId());

        // Check the open expired
        // ProtocolAssert.assertResourceNotFound(!appOpensDb.getExpirationDeleted(), String
        //    .format("Tenant %s open app %s is expired", getOriginalOptTenantId(), appOpen.getAppId()));

        // NOOP:: Check renewal time <- Allow lengthening and reducing renewal date
      }

      @Override
      protected Void process() {
        try {
          AppOpen appOpensDb = appOpenQuery.checkAndFind(appOpen.getAppId(),
              appOpen.getTenantId(), false);
          appOpensDb.setExpirationDate(appOpen.getExpirationDate());
          appOpenRepo.save(appOpensDb);
        } catch (Exception e) {
          if (e instanceof ResourceNotFound) {
            open(appOpen);
          } else {
            throw e;
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancel(Long appId) {
    new BizTemplate<Void>(false) {
      AppOpen appOpensDb;

      @Override
      protected void checkParams() {
        // Check the app existed
        appQuery.checkAndFind(appId, false);
        // Check the open tenantId is required
        assertTrue(hasOriginalOptTenantId(),
            "Open parameter optTenantId is required");
        // Check the application existed
        appOpensDb = appOpenQuery.checkAndFind(appId, getOriginalOptTenantId(), true);
      }

      @Override
      protected Void process() {
        // After deleting the application open record, @CheckAppNotExpired will trigger the verification of non opened applications
        appOpenRepo.delete(appOpensDb);

        // Cancel the opened authorization policies of tenant' users and administrator
        // NOOP:: Retention authorization policies
        authPolicyTenantCmd.appOpenPolicyCancel(getOriginalOptTenantId(), appId);

        log.info("Cancel tenant {} open app {} ", appId, appId);
        return null;
      }
    }.execute();
  }

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

  @Override
  public void open0(AppOpen appOpen, App appDb) {
    // Save app open
    appOpen.setOpClientOpen(isOpClient()).setClientId(appDb.getClientId());
    appOpen.setAppType(appDb.getType()).setExpirationDeleted(false);
    appOpen.setCreatedDate(LocalDateTime.now());
    insert0(appOpen);
  }

  @Override
  public void open0(List<AppOpen> appOpens, App appDb) {
    // Save app open
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
