package cloud.xcan.angus.core.gm.application.query.app.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.APP_FUNC_CODE_EXISTED_T;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.APP_FUNC_IS_DISABLED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isMultiTenantCtrl;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.spec.utils.ObjectUtils.distinctByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncListRepo;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncSearchRepo;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;

/**
 * <p>
 * Implementation of application function query operations.
 * </p>
 * <p>
 * Manages application function retrieval, validation, and hierarchical relationships. Provides
 * comprehensive function querying with parent-child relationship support.
 * </p>
 * <p>
 * Supports function detail retrieval, hierarchical queries, validation checks, and API/tag
 * association for application function management.
 * </p>
 */
@org.springframework.stereotype.Service
public class AppFuncQueryImpl implements AppFuncQuery {

  @Resource
  private AppFuncRepo appFuncRepo;
  @Resource
  private AppFuncListRepo appFuncListRepo;
  @Resource
  private AppFuncSearchRepo appFuncSearchRepo;
  @Resource
  private ApiQuery apiQuery;
  @Resource
  private WebTagQuery webTagQuery;
  @Resource
  private WebTagTargetQuery webTagTargetQuery;

  /**
   * <p>
   * Retrieves detailed application function information by ID.
   * </p>
   * <p>
   * Fetches complete function record with associated APIs and tags. Includes API and tag
   * information for comprehensive function details.
   * </p>
   */
  @Override
  public AppFunc detail(Long id) {
    return new BizTemplate<AppFunc>() {

      @Override
      protected AppFunc process() {
        AppFunc appFunc = checkAndFind(id, false);

        // Associate APIs
        if (isNotEmpty(appFunc.getApiIds())) {
          appFunc.setApis(apiQuery.findAllById(appFunc.getApiIds()));
        }

        // Associate tags
        appFunc.setTags(webTagQuery.findByTargetId(id));
        return appFunc;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves list of application functions with optional full-text search.
   * </p>
   * <p>
   * Supports both regular database queries and full-text search operations. Includes parent
   * functions and target tags for complete function hierarchy.
   * </p>
   */
  @Override
  public List<AppFunc> list(GenericSpecification<AppFunc> spec, boolean fullTextSearch,
      String[] match) {
    return new BizTemplate<List<AppFunc>>() {
      @Override
      protected void checkParams() {
        // NOOP: The query returns empty when the appId does not exist
      }

      @Override
      protected List<AppFunc> process() {
        PageRequest request = PageRequest.of(0, 10000, JpaSort.by(Order.asc("id")));
        Page<AppFunc> page = fullTextSearch
            ? appFuncSearchRepo.find(spec.getCriteria(), request, AppFunc.class, match)
            : appFuncListRepo.find(spec.getCriteria(), request, AppFunc.class, null);

        List<AppFunc> allAppFunc = findAndAllParent(page.getContent().stream()
            .map(x -> x.setHit(true)).collect(Collectors.toList()));
        setTargetTags(allAppFunc);
        return allAppFunc;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves application functions and all their parent functions.
   * </p>
   * <p>
   * Builds complete function hierarchy by recursively finding parent functions. Removes duplicates
   * and returns unique functions with full ancestry.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves application functions by app ID and function IDs.
   * </p>
   * <p>
   * Verifies functions exist for the specified application and optionally checks enabled status.
   * Validates complete collection match and throws appropriate exceptions.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves tenant application functions with multi-tenant control.
   * </p>
   * <p>
   * Temporarily disables multi-tenant control for tenant-level function queries. Validates
   * functions exist and optionally checks enabled status.
   * </p>
   */
  @Override
  public List<AppFunc> checkAndFindTenantAppFunc(Long appId, Collection<Long> funcIds,
      boolean checkEnabled) {
    if (isNull(appId) || isEmpty(funcIds)) {
      return null;
    }

    // Disable multi tenant control temporarily
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    setMultiTenantCtrl(false);
    List<AppFunc> funcDb = checkAndFind(appId, funcIds, checkEnabled);
    setMultiTenantCtrl(isMultiTenantCtrl);
    return funcDb;
  }

  /**
   * <p>
   * Validates function codes do not already exist for the application.
   * </p>
   * <p>
   * Checks for duplicate function codes within the specified application. Throws ResourceExisted
   * exception if any function code already exists.
   * </p>
   */
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

  /**
   * <p>
   * Validates function codes do not conflict during updates.
   * </p>
   * <p>
   * Checks for duplicate function codes excluding the current function being updated. Throws
   * ResourceExisted exception if function code conflicts with other functions.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves application function by ID with optional enabled status check.
   * </p>
   * <p>
   * Verifies function exists and optionally checks enabled status. Throws appropriate exceptions
   * for missing or disabled functions.
   * </p>
   */
  @Override
  public AppFunc checkAndFind(Long id, boolean checkEnabled) {
    AppFunc appFuncDb = appFuncRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "AppFunc"));
    if (checkEnabled) {
      assertTrue(appFuncDb.getEnabled(), APP_FUNC_IS_DISABLED_T, new Object[]{appFuncDb.getName()});
    }
    return appFuncDb;
  }

  /**
   * <p>
   * Validates and retrieves multiple application functions by IDs with optional enabled status
   * check.
   * </p>
   * <p>
   * Verifies all functions exist and optionally checks enabled status. Validates complete
   * collection match and throws appropriate exceptions.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves all application functions by app ID with optional enabled filter.
   * </p>
   * <p>
   * Fetches all functions for the specified application. Optionally filters to only enabled
   * functions when onlyEnabled is true.
   * </p>
   */
  @Override
  public List<AppFunc> findAllByAppId(Long appId, Boolean onlyEnabled) {
    return isNull(onlyEnabled) || !onlyEnabled ? appFuncRepo.findAllByAppId(appId)
        : appFuncRepo.findAllByAppIdAndEnabled(appId, true);
  }

  /**
   * <p>
   * Retrieves application functions by their IDs.
   * </p>
   * <p>
   * Fetches function records for the specified collection of IDs.
   * </p>
   */
  @Override
  public List<AppFunc> findByIdIn(HashSet<Long> funcIds) {
    return appFuncRepo.findAllByIdIn(funcIds);
  }

  /**
   * <p>
   * Retrieves application functions and their sub-functions.
   * </p>
   * <p>
   * Builds complete function hierarchy including all sub-functions recursively. Returns functions
   * and all their descendants in the hierarchy.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves application function and sub-function IDs.
   * </p>
   * <p>
   * Returns IDs of functions and all their descendants in the hierarchy.
   * </p>
   */
  @Override
  public Set<Long> findFuncAndSubIds(Long appId, Collection<Long> funcIds) {
    return findFuncAndSub(appId, funcIds).stream().map(AppFunc::getId).collect(Collectors.toSet());
  }

  /**
   * <p>
   * Retrieves only sub-functions of the specified functions.
   * </p>
   * <p>
   * Returns all descendants of the specified functions without including the parent functions.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves valid application functions by policy IDs.
   * </p>
   * <p>
   * Fetches enabled functions associated with the specified policies.
   * </p>
   */
  @Override
  public List<AppFunc> findValidByPolicyIds(Collection<Long> policyIds) {
    return appFuncRepo.findValidFuncByPolicyId(policyIds);
  }

  /**
   * <p>
   * Retrieves application functions by policy IDs.
   * </p>
   * <p>
   * Fetches all functions associated with the specified policies regardless of status.
   * </p>
   */
  @Override
  public List<AppFunc> findByPolicyIds(Collection<Long> policyIds) {
    return appFuncRepo.findFuncByPolicyId(policyIds);
  }

  /**
   * <p>
   * Associates target tags with application functions.
   * </p>
   * <p>
   * Enriches functions with their associated web tag targets for comprehensive function
   * information.
   * </p>
   */
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

  /**
   * <p>
   * Associates APIs with application functions.
   * </p>
   * <p>
   * Enriches functions with their associated APIs for comprehensive function information.
   * </p>
   */
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

  /**
   * <p>
   * Associates tags with application functions.
   * </p>
   * <p>
   * Enriches functions with their associated web tags for comprehensive function information.
   * </p>
   */
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

  /**
   * <p>
   * Associates APIs with application.
   * </p>
   * <p>
   * Enriches application with its associated APIs for comprehensive application information.
   * </p>
   */
  @Override
  public void setApis(App app) {
    if (nonNull(app) && isNotEmpty(app.getApiIds())) {
      app.setApis(apiQuery.findAllById(app.getApiIds()));
    }
  }
}
