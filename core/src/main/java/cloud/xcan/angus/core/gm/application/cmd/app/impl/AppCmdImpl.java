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
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppCmd;
import cloud.xcan.angus.core.gm.application.cmd.authority.ApiAuthorityCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagTargetCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
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


@Biz
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
  private ClientQuery clientQuery;

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

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public IdKey<Long, Object> add(App app) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Check the code and version is unique
        appQuery.checkUniqueCodeAndVersion(app);
        // Check the client existed
        clientQuery.checkAndFind(app.getClientId());
        // Check the api existed
        apiQuery.checkAndFind(app.getApiIds(), true);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Save application
        IdKey<Long, Object> idKeys = insert(app);

        // Save application tags
        webTagTargetCmd.tag(WebTagTargetType.APP, app.getId(), app.getTagIds());

        // Save application authorities
        apiAuthorityCmd.saveAppApiAuthority(app);

        // Save operation log
        operationLogCmd.add(APP, app, CREATED);
        return idKeys;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void update(App app) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the code and version is unique
        appQuery.checkUniqueCodeAndVersion(app);
        // Check the client existed
        clientQuery.checkAndFind(app.getClientId(), true);
        // Check the api existed
        apiQuery.checkAndFind(app.getApiIds(), true);
      }

      @Override
      protected Void process() {
        // Update the application
        App appDb = updateOrNotFound(app);

        // Update the application tags
        webTagTargetCmd.tag(WebTagTargetType.APP, app.getId(), app.getTagIds());

        // Replace the application authorities
        apiAuthorityCmd.replaceAppApiAuthority(appDb);

        // Save operation log
        operationLogCmd.add(APP, app, UPDATED);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public IdKey<Long, Object> replace(App app) {
    return new BizTemplate<IdKey<Long, Object>>() {
      App replaceAppDb;

      @Override
      protected void checkParams() {
        // Check the updated apis existed
        if (nonNull(app.getId())) {
          replaceAppDb = appQuery.checkAndFind(app.getId(), false);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(app.getId())) {
          return add(app);
        }

        // Update application
        update(copyPropertiesIgnoreTenantAuditing(app, replaceAppDb, "enabled"));

        // Save operation log
        operationLogCmd.add(APP, app, UPDATED);

        return IdKey.of(replaceAppDb.getId(), replaceAppDb.getName());
      }
    }.execute();
  }

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
        // Check the application existed
        appDb = appQuery.checkAndFind(app.getId(), false);
      }

      @Override
      protected Void process() {
        // Update application site information
        appRepo.save(copyPropertiesIgnoreNull(app, appDb));

        // Save operation log
        operationLogCmd.add(APP, app, UPDATED);
        return null;
      }
    }.execute();
  }

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
        appRepo.deleteByIdIn(ids);
        appFuncRepo.deleteByAppIdIn(ids);
        Set<Long> appPolicyIds = authPolicyQuery.findByAppIdIn(ids).stream()
            .map(AuthPolicy::getAppId).collect(Collectors.toSet());
        if (isNotEmpty(appPolicyIds)) {
          authPolicyCmd.delete(appPolicyIds);
        }
        appOpenRepo.deleteByAppIdIn(ids);
        webTagTargetCmd.delete(ids);
        apiAuthorityCmd.deleteBySource(ids, ApiAuthoritySource.APP);

        // Save operation log
        operationLogCmd.addAll(APP, appsDb, DELETED);
        return null;
      }
    }.execute();
  }

  @Override
  public void enabled(List<App> apps) {
    new BizTemplate<Void>() {
      List<App> appsDb;
      Set<Long> ids;

      @Override
      protected void checkParams() {
        // Check the applications existed
        ids = apps.stream().map(App::getId).collect(Collectors.toSet());
        appsDb = appQuery.checkAndFind(ids, false);
        // Check for unused apps in store
        Map<Long, App> appsMap = apps.stream().collect(Collectors.toMap(App::getId, x -> x));
        for (App appDb : appsDb) {
          appDb.setEnabled(appsMap.get(appDb.getId()).getEnabled());
        }
      }

      @Override
      protected Void process() {
        // Update apps enabled status
        appRepo.saveAll(appsDb);

        // Sync update authority status
        apiAuthorityCmd.updateAppAuthorityStatus(apps, ids);

        // Save operation log
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
