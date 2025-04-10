package cloud.xcan.angus.core.gm.application.query.app.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_FUNC_CODE_EXISTED_T;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_FUNC_CODE_REPEATED_T;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.APP_FUNC_IS_DISABLED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isMultiTenantCtrl;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.spec.utils.ObjectUtils.distinctByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncListRepo;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;


@Biz
public class AppFuncQueryImpl implements AppFuncQuery {

  @Resource
  private AppFuncRepo appFuncRepo;

  @Resource
  private AppFuncListRepo appFuncListRepo;

  @Resource
  private ApiQuery apiQuery;

  @Resource
  private WebTagQuery webTagQuery;

  @Resource
  private WebTagTargetQuery webTagTargetQuery;

  @Override
  public AppFunc detail(Long id) {
    return new BizTemplate<AppFunc>() {

      @Override
      protected AppFunc process() {
        AppFunc appFunc = checkAndFind(id, false);

        // Join api
        if (isNotEmpty(appFunc.getApiIds())) {
          appFunc.setApis(apiQuery.findAllById(appFunc.getApiIds()));
        }

        // Join tag
        appFunc.setTags(webTagQuery.findByTargetId(id));
        return appFunc;
      }
    }.execute();
  }

  @Override
  public List<AppFunc> list(GenericSpecification<AppFunc> spec) {
    return new BizTemplate<List<AppFunc>>() {
      @Override
      protected void checkParams() {
        // NOOP: The query returns empty when the appId does not exist
      }

      @Override
      protected List<AppFunc> process() {
        List<AppFunc> func = appFuncListRepo.find(spec.getCriteria(),
            PageRequest.of(0, 10000, JpaSort.by(Order.asc("id"))),
            AppFunc.class, null).getContent();
        List<AppFunc> allAppFunc = findAndAllParent(func.stream().map(x -> x.setHit(true))
            .collect(Collectors.toList()));
        setTargetTags(allAppFunc);
        return allAppFunc;
      }
    }.execute();
  }

  @Override
  public List<AppFunc> findAndAllParent(Collection<AppFunc> func) {
    List<AppFunc> allAppFunc = new ArrayList<>();
    if (isNotEmpty(func)) {
      allAppFunc.addAll(func);
      Set<Long> parentAppFuncIds = func.stream().filter(AppFunc::hasParent)
          .map(AppFunc::getPid).collect(Collectors.toSet());
      do {
        List<AppFunc> parentAppFunc = appFuncRepo.findAllById(parentAppFuncIds);
        if (isNotEmpty(parentAppFunc)) {
          allAppFunc.addAll(parentAppFunc);
          parentAppFuncIds = parentAppFunc.stream().filter(AppFunc::hasParent)
              .map(AppFunc::getPid).collect(Collectors.toSet());
        }
      } while (!parentAppFuncIds.isEmpty());
      return allAppFunc.stream().filter(distinctByKey(AppFunc::getCode))
          .collect(Collectors.toList());
    }
    return allAppFunc;
  }

  @Override
  public List<AppFunc> checkAndFind(Long appId, Collection<Long> funcIds, boolean checkEnabled) {
    if (isNull(appId) || isEmpty(funcIds)) {
      return null;
    }
    List<AppFunc> func = appFuncRepo.findByAppIdAndIdIn(appId, funcIds);
    assertResourceNotFound(isNotEmpty(func), funcIds.iterator().next(), "AppFunc");
    if (funcIds.size() != func.size()) {
      for (AppFunc func0 : func) {
        assertResourceNotFound(funcIds.contains(func0.getId()), func0.getId(), "AppFunc");
      }
    }
    if (checkEnabled) {
      for (AppFunc appFunc : func) {
        assertTrue(appFunc.getEnabled(), APP_FUNC_IS_DISABLED_T, new Object[]{appFunc.getName()});
      }
    }
    return func;
  }

  @Override
  public List<AppFunc> checkAndFindTenantAppFunc(Long appId, Collection<Long> funcIds,
      boolean checkEnabled) {
    if (isNull(appId) || isEmpty(funcIds)) {
      return null;
    }

    // Turn off multi tenant control
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    setMultiTenantCtrl(false);
    List<AppFunc> funcDb = checkAndFind(appId, funcIds, checkEnabled);
    setMultiTenantCtrl(isMultiTenantCtrl);
    return funcDb;
  }

  @Override
  public void checkAddCodeExist(Long appId, List<AppFunc> func) {
    List<String> appFuncCodes = func.stream().map(AppFunc::getCode)
        .collect(Collectors.toList());
    List<String> existedCodes = appFuncRepo.findByAppIdAndCodeIn(appId, appFuncCodes)
        .stream().map(AppFunc::getCode).collect(Collectors.toList());
    if (isNotEmpty(existedCodes)) {
      throw ResourceExisted.of(APP_FUNC_CODE_EXISTED_T, new Object[]{existedCodes.get(0)});
    }
  }

  @Override
  public void checkUpdateCodeExist(Long appId, List<AppFunc> func) {
    List<String> appFuncCodes = func.stream().map(AppFunc::getCode)
        .collect(Collectors.toList());
    List<AppFunc> funcDb = appFuncRepo.findByAppIdAndCodeIn(appId, appFuncCodes);
    if (isNotEmpty(funcDb)) {
      funcDb.forEach(appFuncDb -> {
        func.forEach(func0 -> {
          if (appFuncDb.getCode().equals(func0.getCode())
              && !appFuncDb.getId().equals(func0.getId())) {
            throw ResourceExisted.of(APP_FUNC_CODE_EXISTED_T, new Object[]{appFuncDb.getCode()});
          }
        });
      });
    }
  }

  @Override
  public AppFunc checkAndFind(Long id, boolean checkEnabled) {
    AppFunc appFuncDb = appFuncRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "AppFunc"));
    if (checkEnabled) {
      assertTrue(appFuncDb.getEnabled(), APP_FUNC_IS_DISABLED_T, new Object[]{appFuncDb.getName()});
    }
    return appFuncDb;
  }

  @Override
  public List<AppFunc> checkAndFind(Collection<Long> ids, boolean checkEnabled) {
    if (isEmpty(ids)) {
      return null;
    }
    List<AppFunc> func = appFuncRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(func), ids.iterator().next(), "AppFunc");

    if (ids.size() != func.size()) {
      for (AppFunc func0 : func) {
        assertResourceNotFound(ids.contains(func0.getId()), func0.getId(), "AppFunc");
      }
    }

    if (checkEnabled) {
      for (AppFunc appFunc : func) {
        assertTrue(appFunc.getEnabled(), APP_FUNC_IS_DISABLED_T, new Object[]{appFunc.getName()});
      }
    }
    return func;
  }

  @Override
  public List<AppFunc> findAllByAppId(Long appId, Boolean onlyEnabled) {
    return isNull(onlyEnabled) || !onlyEnabled ? appFuncRepo.findAllByAppId(appId)
        : appFuncRepo.findAllByAppIdAndEnabled(appId, true);
  }

  @Override
  public List<AppFunc> findByIdIn(HashSet<Long> funcIds) {
    return appFuncRepo.findAllByIdIn(funcIds);
  }

  @Override
  public List<AppFunc> findFuncAndSub(Long appId, Collection<Long> funcIds) {
    if (isEmpty(funcIds)) {
      return Collections.emptyList();
    }
    // Find current functions
    List<AppFunc> funcs = appFuncRepo.findByAppIdAndIdIn(appId, funcIds);
    if (isEmpty(funcs)) {
      return Collections.emptyList();
    }
    List<AppFunc> allFuncAndSub = new ArrayList<>(funcs);
    do {
      // Find sub functions
      funcs = appFuncRepo.findByAppIdAndPidIn(appId, funcIds);
      if (isNotEmpty(funcs)) {
        allFuncAndSub.addAll(funcs);
        funcIds = funcs.stream().map(AppFunc::getId).collect(Collectors.toSet());
      }
    } while (isNotEmpty(funcs));
    return allFuncAndSub;
  }

  @Override
  public Set<Long> findFuncAndSubIds(Long appId, Collection<Long> funcIds) {
    return findFuncAndSub(appId, funcIds).stream().map(AppFunc::getId).collect(Collectors.toSet());
  }

  @Override
  public List<AppFunc> findSub(Long appId, Collection<Long> funcIds) {
    if (isEmpty(funcIds)) {
      return Collections.emptyList();
    }
    List<AppFunc> allFuncAndSub = new ArrayList<>();
    List<AppFunc> appFuncSubs;
    do {
      // Find sub functions
      appFuncSubs = appFuncRepo.findByAppIdAndPidIn(appId, funcIds);
      if (isNotEmpty(appFuncSubs)) {
        allFuncAndSub.addAll(appFuncSubs);
        funcIds = appFuncSubs.stream().map(AppFunc::getId).collect(Collectors.toList());
      }
    } while (isNotEmpty(appFuncSubs));
    return allFuncAndSub;
  }

  @Override
  public List<Long> findSubIds(Long appId, Collection<Long> funcIds) {
    return findSub(appId, funcIds).stream().map(AppFunc::getId).collect(Collectors.toList());
  }

  @Override
  public List<AppFunc> findValidByPolicyIds(Collection<Long> policyIds) {
    return appFuncRepo.findValidFuncByPolicyId(policyIds);
  }

  @Override
  public List<AppFunc> findByPolicyIds(Collection<Long> policyIds) {
    return appFuncRepo.findFuncByPolicyId(policyIds);
  }

  @Override
  public void setTargetTags(List<AppFunc> func) {
    if (isNotEmpty(func)) {
      List<Long> allFuncIds = func.stream().map(AppFunc::getId).collect(Collectors.toList());
      Map<Long, List<WebTagTarget>> tagTargetMap = webTagTargetQuery
          .findTargetTagByTargetId(allFuncIds);
      if (isNotEmpty(tagTargetMap)) {
        for (AppFunc func0 : func) {
          func0.setTagTargets(tagTargetMap.get(func0.getId()));
        }
      }
    }
  }

  @Override
  public void setApis(List<AppFunc> func) {
    if (isNotEmpty(func)) {
      Set<Long> allApiIds = func.stream().filter(x -> isNotEmpty(x.getApiIds()))
          .flatMap(a -> a.getApiIds().stream()).collect(Collectors.toSet());
      if (isEmpty(allApiIds)) {
        return;
      }
      List<Api> apis = apiQuery.findAllById(allApiIds);
      if (isNotEmpty(apis)) {
        for (AppFunc func0 : func) {
          if (isNotEmpty(func0.getApiIds())) {
            func0.setApis(apis.stream().filter(x -> func0.getApiIds().contains(x.getId()))
                .collect(Collectors.toList()));
          }
        }
      }
    }
  }

  @Override
  public void setTags(List<AppFunc> func) {
    if (isNotEmpty(func)) {
      Set<Long> allApiIds = func.stream().map(AppFunc::getId).collect(Collectors.toSet());
      Map<Long, List<WebTag>> tagsMap = webTagQuery.findByTargetIdIn(allApiIds);
      if (isNotEmpty(tagsMap)) {
        for (AppFunc func0 : func) {
          func0.setTags(tagsMap.get(func0.getId()));
        }
      }
    }
  }

  @Override
  public void setApis(App app) {
    if (nonNull(app) && isNotEmpty(app.getApiIds())) {
      app.setApis(apiQuery.findAllById(app.getApiIds()));
    }
  }
}
