package cloud.xcan.angus.core.gm.application.cmd.app.impl;

import static cloud.xcan.angus.core.gm.application.converter.ApiAuthorityConverter.toFuncAuthority;
import static cloud.xcan.angus.core.gm.application.converter.OperationLogConverter.toOperations;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP;
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(Long appId, List<AppFunc> appFunc) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      App appDb;

      @Override
      protected void checkParams() {
        // Check the application existed
        appDb = appQuery.checkAndFind(appId, true);
        // Check the pid function exists under same application
        appFuncQuery.checkAndFind(appId, appFunc.stream().filter(AppFunc::hasParent)
            .map(AppFunc::getPid).collect(Collectors.toSet()), true);
        // Check functions code whether unique under same application
        appFuncQuery.checkAddCodeExist(appId, appFunc);
        // Check the api existed
        // NOOP: Done in saveFuncApiAuthority()
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Set application info
        setFuncAppInfo(appDb, appFunc);
        // Add functions
        List<IdKey<Long, Object>> idKeys = batchInsert(appFunc, "code");

        // Save functions tags
        appFunc.forEach(func -> {
          webTagTargetCmd.tag(func.getType().toTagTargetType(), func.getId(), func.getTagIds());
        });

        // Save apis authority
        saveFuncApiAuthority(appDb, appFunc);

        // Save operation log
        operationLogCmd.addAll(toOperations(APP_FUNC, appFunc, CREATED));
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      App appDb;

      @Override
      protected void checkParams() {
        // Check the application existed
        appDb = appQuery.checkAndFind(appId, true);
        // Check the application functions existed
        appFuncQuery.checkAndFind(appId, appFunc.stream().map(AppFunc::getId)
            .collect(Collectors.toSet()), false);
        // Check the pid function exists under same application
        appFuncQuery.checkAndFind(appId, appFunc.stream().filter(AppFunc::hasParent)
            .map(AppFunc::getPid).collect(Collectors.toSet()), false);
        // Check the functions code whether unique under same application
        appFuncQuery.checkUpdateCodeExist(appId, appFunc);
        // Check the api existed
        // NOOP: Done in saveFuncApiAuthority()
      }

      @Override
      protected Void process() {
        // Update functions
        List<AppFunc> appFuncDb = batchUpdateOrNotFound(appFunc);

        // Update functions tags
        Map<Long, AppFunc> appFuncsMap = appFuncDb.stream()
            .collect(Collectors.toMap(AppFunc::getId, x -> x));
        appFunc.forEach(func -> {
          webTagTargetCmd.tag(appFuncsMap.get(func.getId()).getType().toTagTargetType(),
              func.getId(), func.getTagIds());
        });

        // Replace functions authorities
        replaceFuncApiAuthority(appDb, appFunc);

        // Save operation log
        operationLogCmd.addAll(toOperations(APP_FUNC, appFuncDb, UPDATED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      List<AppFunc> replaceFunc;
      List<AppFunc> replaceFuncDb;

      @Override
      protected void checkParams() {
        // Check the updated functions existed
        replaceFunc = appFunc.stream().filter(func -> Objects.nonNull(func.getId()))
            .collect(Collectors.toList());
        replaceFuncDb = appFuncQuery.checkAndFind(appId, replaceFunc.stream()
            .map(AppFunc::getId).collect(Collectors.toSet()), false);
      }

      @Override
      protected Void process() {
        List<AppFunc> addFunc = appFunc.stream().filter(app -> Objects.isNull(app.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addFunc)) {
          add(appId, addFunc);
        }

        if (isNotEmpty(replaceFunc)) {
          Map<Long, AppFunc> appFuncMap = replaceFuncDb.stream()
              .collect(Collectors.toMap(AppFunc::getId, x -> x));
          // Do not replace enabled and type
          update(appId, replaceFunc.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, appFuncMap.get(x.getId()),
                  "enabled", "type"))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long appId, HashSet<Long> funcIds) {
    new BizTemplate<Void>() {
      List<AppFunc> appFuncDb;

      @Override
      protected void checkParams() {
        // Check the functions existed
        appFuncDb = appFuncQuery.checkAndFind(appId, funcIds, false);
      }

      @Override
      protected Void process() {
        Set<Long> existedFuncAndSubIds = appFuncQuery.findFuncAndSubIds(appId, funcIds);
        if (isNotEmpty(existedFuncAndSubIds)) {
          appFuncRepo.deleteByAppIdAndIdIn(appId, existedFuncAndSubIds);
          webTagTargetCmd.delete(existedFuncAndSubIds);
          apiAuthorityRepo.deleteBySourceIdInAndSource(existedFuncAndSubIds,
              ApiAuthoritySource.APP_FUNC.getValue());
        }

        // Save operation log
        operationLogCmd.addAll(toOperations(APP_FUNC, appFuncDb, DELETED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long appId, List<AppFunc> appFunc) {
    new BizTemplate<Void>() {
      List<AppFunc> appFuncDb;
      Set<Long> ids;

      @Override
      protected void checkParams() {
        // Check the functions existed
        ids = appFunc.stream().map(AppFunc::getId).collect(Collectors.toSet());
        appFuncDb = appFuncQuery.checkAndFind(appId, ids, false);
      }

      @Override
      protected Void process() {
        // Cascading disable the sub function when parent function is disabled
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

        // Update function and sub authority to disabled
        if (isNotEmpty(disabledAppFuncIds)) {
          if (isNotEmpty(allSubs)) {
            disabledAppFuncIds.addAll(allSubs.stream().map(AppFunc::getId).toList());
          }
          updateAppFuncAuthorityStatus(disabledAppFuncIds, false);
        }

        // Update function enabled or disabled status
        batchUpdateOrNotFound(appFunc);

        // Save operation log
        operationLogCmd.addAll(toOperations(APP_FUNC,
            appFuncDb.stream().filter(AppFunc::getEnabled).toList(), ENABLED));
        operationLogCmd.addAll(toOperations(APP_FUNC,
            appFuncDb.stream().filter(x -> !x.getEnabled()).toList(), DISABLED));
        return null;
      }
    }.execute();
  }

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

  private void replaceFuncApiAuthority(App app, List<AppFunc> appFunc) {
    if (isNotEmpty(appFunc) && appFunc.stream().anyMatch(x -> isNotEmpty(x.getApiIds()))) {
      deleteFuncApiAuthority(appFunc);
      saveFuncApiAuthority(app, appFunc);
    }
  }

  private void deleteFuncApiAuthority(List<AppFunc> appFunc) {
    Set<Long> funcIds = appFunc.stream().map(AppFunc::getId).collect(Collectors.toSet());
    if (isNotEmpty(funcIds)) {
      apiAuthorityRepo.deleteBySourceIdInAndSource(funcIds, ApiAuthoritySource.APP_FUNC.getValue());
    }
  }

  private void saveFuncApiAuthority(App app, List<AppFunc> appFunc) {
    List<ApiAuthority> authorities = new ArrayList<>();
    for (AppFunc func : appFunc) {
      if (isNotEmpty(func.getApiIds())) {
        // Check the api existed
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

  private void setFuncAppInfo(App appDb, List<AppFunc> appFunc) {
    appFunc.forEach(func -> {
      func.setClientId(appDb.getClientId()).setTenantId(appDb.getTenantId());
    });
  }

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
