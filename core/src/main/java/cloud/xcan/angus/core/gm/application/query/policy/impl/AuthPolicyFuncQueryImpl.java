package cloud.xcan.angus.core.gm.application.query.policy.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.CoreUtils.distinct;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyFuncQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncRepo;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFunc;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFuncRepo;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Biz
public class AuthPolicyFuncQueryImpl implements AuthPolicyFuncQuery {

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private AuthPolicyFuncRepo authPolicyFuncRepo;

  @Resource
  private AppFuncRepo appFuncRepo;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Resource
  private AppOpenQuery appOpenQuery;

  @Resource
  private ApiQuery apiQuery;

  @NameJoin
  @Override
  public List<AppFunc> list(Long policyId) {
    return new BizTemplate<List<AppFunc>>(false) {
      @Override
      protected void checkParams() {
        // Check policy existed
        AuthPolicy policiesDb = authPolicyQuery.checkAndFind(policyId, false, false);
        // Check policy tenant is consistent
        if (policiesDb.isUserDefined()) {
          assertResourceNotFound(policiesDb.getTenantId().equals(getTenantId()),
              policyId, "Policy");
        } else {
          // Check policy app is opened when PRE_DEFINED
          appOpenQuery.checkAndFind(policiesDb.getAppId(), policiesDb.getTenantId(), false);
        }
      }

      @Override
      protected List<AppFunc> process() {
        List<AppFunc> appFunc = appFuncQuery.findByPolicyIds(List.of(policyId));
        // Join apis
        joinAppFuncApis(appFunc);
        return appFunc;
      }
    }.execute();
  }

  @Override
  public List<AuthPolicyFunc> findByPolicyId(Long policyId) {
    return authPolicyFuncRepo.findByPolicyId(policyId);
  }

  @Override
  public List<AuthPolicyFunc> findByPolicyId(Collection<Long> policyIds) {
    return distinct(authPolicyFuncRepo.findByPolicyIdIn(policyIds));
  }

  @Override
  public List<AppFunc> findValidFuncByPolicyId(Collection<Long> policyIds) {
    return appFuncRepo.findValidFuncByPolicyId(policyIds);
  }

  @Override
  public Set<Long> findExistedFuncIdsByPolicyId(Long policyId) {
    return authPolicyFuncRepo.findFuncIdsByPolicyId(policyId);
  }

  private void joinAppFuncApis(List<AppFunc> appFunc) {
    Set<Long> apiIds = new HashSet<>();
    for (AppFunc appFunc0 : appFunc) {
      if (isNotEmpty(appFunc0.getApiIds())) {
        apiIds.addAll(appFunc0.getApiIds());
      }
    }
    List<Api> apis = apiQuery.findAllById(apiIds);
    if (isNotEmpty(apis)) {
      for (AppFunc appFunc0 : appFunc) {
        if (isNotEmpty(appFunc0.getApiIds())) {
          appFunc0.setApis(apis.stream().filter(x -> appFunc0.getApiIds().contains(x.getId()))
              .collect(Collectors.toList()));
        }
      }
    }
  }
}
