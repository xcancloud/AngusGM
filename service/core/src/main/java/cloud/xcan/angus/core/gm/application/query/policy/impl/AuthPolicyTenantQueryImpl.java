package cloud.xcan.angus.core.gm.application.query.policy.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyTenantQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Biz
public class AuthPolicyTenantQueryImpl implements AuthPolicyTenantQuery {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AppQuery appQuery;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private AppOpenQuery appOpenQuery;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Override
  public List<AuthPolicyOrg> defaultPolicy() {
    return new BizTemplate<List<AuthPolicyOrg>>(false) {
      final Long optTenantId = getOptTenantId();


      @Override
      protected List<AuthPolicyOrg> process() {
        // closeMultiTenantCtrl(); <- For query PRE_DEFINED policies
        List<AuthPolicy> allDefaultPolicies = authPolicyQuery
            .findTenantAppDefaultPolices(optTenantId);
        if (isEmpty(allDefaultPolicies)) {
          return Collections.emptyList();
        }

        // The current default policies is empty when to prevent new users from joining
        Map<Long, AuthPolicyOrg> currentDefaultPoliciesMap = authPolicyOrgRepo
            .findByTenantIdAndDefault0(optTenantId, true)
            .stream().collect(Collectors.toMap(AuthPolicyOrg::getPolicyId, x -> x));

        Map<Long, List<AuthPolicy>> appDefaultPoliciesMap = allDefaultPolicies.stream()
            .peek(x -> x.setCurrentDefault0(currentDefaultPoliciesMap.containsKey(x.getId())))
            .collect(Collectors.groupingBy(AuthPolicy::getAppId));

        Map<Long, App> openedApps = appQuery.checkAndFind(appDefaultPoliciesMap.keySet(), false)
            .stream().collect(Collectors.toMap(App::getId, x -> x));

        List<AuthPolicyOrg> wrappers = new ArrayList<>(appDefaultPoliciesMap.keySet().size());
        for (Long appId : appDefaultPoliciesMap.keySet()) {
          App openedApp = openedApps.get(appId);
          if (nonNull(openedApp) && getClientId().equals(openedApp.getClientId())) {
            assertResourceNotFound(openedApps.get(appId), appId, "OpenApp");
            assertResourceNotFound(appDefaultPoliciesMap.get(appId), appId, "AppPolicies");
            wrappers.add(new AuthPolicyOrg().setApp(openedApps.get(appId))
                .setDefaultPolices(appDefaultPoliciesMap.get(appId)));
          }
        }
        return wrappers;
      }
    }.execute();
  }

  @Override
  public List<App> tenantAppList() {
    return new BizTemplate<List<App>>() {

      @Override
      protected List<App> process() {
        List<AppOpen> openApps = appOpenQuery.findOpenedAppByTenantId(getOptTenantId());
        if (isEmpty(openApps)) {
          return Collections.emptyList();
        }
        return appQuery.checkAndFind(openApps.stream().map(AppOpen::getAppId)
            .collect(Collectors.toSet()), true);
      }
    }.execute();
  }

  @Override
  public List<AppFunc> tenantAppFuncList(Long appId) {
    return new BizTemplate<List<AppFunc>>() {
      @Override
      protected void checkParams() {
        // Check the opened application existed
        appOpenQuery.checkAndFind(appId, getOptTenantId(), false);
      }

      @Override
      protected List<AppFunc> process() {
        AuthPolicyOrg openAuth = findAdminOpenAuthByAppIdAndTenantId(appId, getOptTenantId());
        return appFuncQuery.findValidByPolicyIds(List.of(openAuth.getPolicyId()));
      }
    }.execute();
  }

  @Override
  public AuthPolicyOrg findAdminOpenAuthByAppIdAndTenantId(Long appId, Long tenantId) {
    return authPolicyOrgRepo.findAdminOpenAuthByAppIdAndTenantId(appId, tenantId)
        .orElseThrow(() -> ResourceNotFound.of(appId, "OpenAuthPolicy"));
  }

}
