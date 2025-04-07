package cloud.xcan.angus.core.gm.application.cmd.app.impl;

import static cloud.xcan.angus.core.gm.application.converter.ApiAuthorityConverter.toFuncAuthority;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppFuncCmd;
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
  private ApiAuthorityRepo authorityRepo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(Long appId, List<AppFunc> appFuncs) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      App appDb;

      @Override
      protected void checkParams() {
        // Check the application existed
        appDb = appQuery.checkAndFind(appId, true);
        // Check the pid function exists under same application
        appFuncQuery.checkAndFind(appId, appFuncs.stream().filter(AppFunc::hasParent)
            .map(AppFunc::getPid).collect(Collectors.toSet()), true);
        // Check functions code whether unique under same application
        appFuncQuery.checkAddCodeExist(appId, appFuncs);
        // Check the api existed
        // NOOP: Done in saveFuncApiAuthority()
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Complete functions info
        completeFuncAppInfo(appDb, appFuncs);
        // Add functions
        List<IdKey<Long, Object>> idKeys = batchInsert(appFuncs, "code");

        // Save functions tags
        appFuncs.forEach(func -> {
          webTagTargetCmd.tag(func.getType().toTagTargetType(), func.getId(), func.getTagIds());
        });

        // Save apis authority
        saveFuncApiAuthority(appDb, appFuncs);
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Long appId, List<AppFunc> appFuncs) {
    new BizTemplate<Void>() {
      App appDb;

      @Override
      protected void checkParams() {
        // Check the application existed
        appDb = appQuery.checkAndFind(appId, true);
        // Check the application functions existed
        appFuncQuery.checkAndFind(appId, appFuncs.stream().map(AppFunc::getId)
            .collect(Collectors.toSet()), false);
        // Check the pid function exists under same application
        appFuncQuery.checkAndFind(appId, appFuncs.stream().filter(AppFunc::hasParent)
            .map(AppFunc::getPid).collect(Collectors.toSet()), false);
        // Check the functions code whether unique under same application
        appFuncQuery.checkUpdateCodeExist(appId, appFuncs);
        // Check the api existed
        // NOOP: Done in saveFuncApiAuthority()
      }

      @Override
      protected Void process() {
        // Update functions
        List<AppFunc> appFuncsDb = batchUpdateOrNotFound(appFuncs);

        // Update functions tags
        Map<Long, AppFunc> appFuncsMap = appFuncsDb.stream()
            .collect(Collectors.toMap(AppFunc::getId, x -> x));
        appFuncs.forEach(func -> {
          webTagTargetCmd.tag(appFuncsMap.get(func.getId()).getType().toTagTargetType(),
              func.getId(), func.getTagIds());
        });

        // Replace functions authorities
        replaceFuncApiAuthority(appDb, appFuncs);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(Long appId, List<AppFunc> appFuncs) {
    new BizTemplate<Void>() {
      List<AppFunc> replaceFuncs;
      List<AppFunc> replaceFuncsDb;

      @Override
      protected void checkParams() {
        // Check the updated apis existed
        replaceFuncs = appFuncs.stream().filter(func -> Objects.nonNull(func.getId()))
            .collect(Collectors.toList());
        replaceFuncsDb = appFuncQuery.checkAndFind(appId, replaceFuncs.stream()
            .map(AppFunc::getId).collect(Collectors.toSet()), false);
      }

      @Override
      protected Void process() {
        List<AppFunc> addFuncs = appFuncs.stream().filter(app -> Objects.isNull(app.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addFuncs)) {
          add(appId, addFuncs);
        }

        if (isNotEmpty(replaceFuncs)) {
          Map<Long, AppFunc> appFuncMap = replaceFuncsDb.stream()
              .collect(Collectors.toMap(AppFunc::getId, x -> x));
          // Do not replace enabled and type
          update(appId, replaceFuncs.stream()
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
      @Override
      protected Void process() {
        Set<Long> existedFuncAndSubIds = appFuncQuery.findFuncAndSubIds(appId, funcIds);
        if (isNotEmpty(existedFuncAndSubIds)) {
          appFuncRepo.deleteByAppIdAndIdIn(appId, existedFuncAndSubIds);
          webTagTargetCmd.delete(existedFuncAndSubIds);
          authorityRepo.deleteBySourceIdInAndSource(existedFuncAndSubIds,
              ApiAuthoritySource.APP_FUNC.getValue());
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long appId, List<AppFunc> appFuncs) {
    new BizTemplate<Void>() {
      List<AppFunc> appFuncsDb;
      Set<Long> ids;

      @Override
      protected void checkParams() {
        // Check the application functions existed
        ids = appFuncs.stream().map(AppFunc::getId).collect(Collectors.toSet());
        appFuncsDb = appFuncQuery.checkAndFind(appId, ids, false);
      }

      @Override
      protected Void process() {
        // Cascading disable the sub function when parent function is disabled
        Set<Long> disabledAppFuncIds = appFuncs.stream().filter(x -> !x.getEnabled())
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
        Set<Long> enabledAppFuncIds = appFuncs.stream().filter(AppFunc::getEnabled)
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
        batchUpdateOrNotFound(appFuncs);
        return null;
      }
    }.execute();
  }

  @Override
  public void importFuncs(Long appId, List<AppFunc> appFuncs) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        replace(appId, appFuncs);
        return null;
      }
    }.execute();
  }

  private void replaceFuncApiAuthority(App app, List<AppFunc> funcs) {
    if (isNotEmpty(funcs) && funcs.stream().anyMatch(x -> isNotEmpty(x.getApiIds()))) {
      deleteFuncApiAuthority(funcs);
      saveFuncApiAuthority(app, funcs);
    }
  }

  private void deleteFuncApiAuthority(List<AppFunc> funcs) {
    Set<Long> funcIds = funcs.stream().map(AppFunc::getId).collect(Collectors.toSet());
    if (isNotEmpty(funcIds)) {
      authorityRepo.deleteBySourceIdInAndSource(funcIds, ApiAuthoritySource.APP_FUNC.getValue());
    }
  }

  private void saveFuncApiAuthority(App app, List<AppFunc> funcs) {
    List<ApiAuthority> authorities = new ArrayList<>();
    for (AppFunc func : funcs) {
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
      authorityRepo.batchInsert(authorities);
    }
  }

  private void completeFuncAppInfo(App appDb, List<AppFunc> appFuncs) {
    appFuncs.forEach(appFunc -> {
      appFunc.setClientId(appDb.getClientId()).setTenantId(appDb.getTenantId());
    });
  }

  private void updateAppFuncAuthorityStatus(Collection<Long> ids, boolean enabled) {
    List<ApiAuthority> authorities = authorityRepo.findByAppIdIn(ids);
    if (isNotEmpty(authorities)) {
      for (ApiAuthority authority : authorities) {
        authority.setAppEnabled(enabled);
      }
      authorityRepo.saveAll(authorities);
    }
  }

  @Override
  protected BaseRepository<AppFunc, Long> getRepository() {
    return this.appFuncRepo;
  }
}
