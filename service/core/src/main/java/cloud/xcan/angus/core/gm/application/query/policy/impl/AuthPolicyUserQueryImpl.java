package cloud.xcan.angus.core.gm.application.query.policy.impl;

import static cloud.xcan.angus.api.commonlink.AASConstant.OP_PLATFORM_ADMIN;
import static cloud.xcan.angus.api.commonlink.AASConstant.TOP_PERMISSION_ADMIN;
import static cloud.xcan.angus.api.commonlink.AuthOrgType.USER;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasAnyPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isSysAdmin;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyUserQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthOrgPolicyListRepo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthUserAssocPolicyListRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndCode;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;


@Biz
public class AuthPolicyUserQueryImpl implements AuthPolicyUserQuery {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AuthOrgPolicyListRepo authOrgPolicyListRepo;

  @Resource
  private AuthUserAssocPolicyListRepo authUserAssocPolicyListRepo;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private UserRepo userRepo;

  @Resource
  private AppQuery appQuery;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Resource
  private UserManager userManager;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  public Page<User> policyUserList(GenericSpecification<User> spec, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      String policyId;

      @Override
      protected void checkParams() {
        policyId = findFirstValueAndRemove(spec.getCriteria(), "policyId", EQUAL);
        assertNotEmpty(policyId, "Parameter policyId is required");
        closeMultiTenantCtrl(); // May be default policy
        authPolicyQuery.checkAndFind(Long.valueOf(policyId), false, false);
        enableMultiTenantCtrl();
      }

      @Override
      protected Page<User> process() {
        List<Long> userIds = authPolicyOrgRepo.finOrgIdsByPolicyIdAndOrgType(
            Long.valueOf(policyId), USER.getValue());
        if (isEmpty(userIds)) {
          return Page.empty();
        }
        spec.getCriteria().add(SearchCriteria.in("id", userIds));
        return userRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<User> policyUnauthUserList(GenericSpecification<User> spec, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      String policyId;

      @Override
      protected void checkParams() {
        policyId = findFirstValueAndRemove(spec.getCriteria(), "policyId", EQUAL);
        assertNotEmpty(policyId, "Parameter policyId is required");
        authPolicyQuery.checkAndFind(Long.valueOf(policyId), false, false);
      }

      @Override
      protected Page<User> process() {
        List<Long> userIds = authPolicyOrgRepo.finOrgIdsByPolicyIdAndOrgType(
            Long.valueOf(policyId), USER.getValue());
        if (isNotEmpty(userIds)) {
          spec.getCriteria().add(SearchCriteria.notIn("id", userIds));
        }
        return userRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuthPolicy> userPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {
      String orgId;
      User orgUser;

      @Override
      protected void checkParams() {
        orgId = findFirstValueAndRemove(spec.getCriteria(), "orgId", EQUAL);
        assertNotEmpty(orgId, "Parameter orgId is required");
        String orgType = findFirstValue(spec.getCriteria(), "orgType", EQUAL);
        assertNotEmpty(orgType, "Parameter orgType is required");
        // enableMultiTenantCtrl();
        // Check user exists
        if (!getUserId().equals(Long.valueOf(orgId))) {
          orgUser = (User) userManager.checkOrgAndFind(OrgTargetType.USER, Long.valueOf(orgId));
        }
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();

        boolean isSysAdmin = judgeAdmin(spec, orgUser, false);

        //Fix:: Only query orgType=USER
        //List<Long> orgIds = userManager.getValidOrgAndUserIds(Long.valueOf(orgId));
        //orgIds.add(getOptTenantId());
        spec.getCriteria().add(SearchCriteria.in("orgId",
            List.of(Long.parseLong(orgId), getOptTenantId())));
        spec.getCriteria().add(SearchCriteria.equal("isSysAdmin", isSysAdmin));
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));

        boolean ignoreAuthOrg = getIgnoreAuthOrg(spec);

        return authOrgPolicyListRepo.find(spec.getCriteria(), pageable,
            AuthPolicy.class, ignoreAuthOrg ? AuthPolicyOrgConverter::objectArrToOrgAuthPolicy :
                AuthPolicyOrgConverter::objectArrToOrgAuthPolicyAndOrg, null);
      }
    }.execute();
  }

  @Override
  public Page<AuthPolicy> userAssociatedPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable, boolean currentAdmin) {
    return new BizTemplate<Page<AuthPolicy>>() {
      String orgId;
      User orgUser;

      @Override
      protected void checkParams() {
        orgId = findFirstValue(spec.getCriteria(), "orgId", EQUAL);
        assertNotEmpty(orgId, "Parameter userId is required");
        // enableMultiTenantCtrl();

        // Check user existed, it is required for judgeAdmin()
        orgUser = (User) userManager.checkOrgAndFind(OrgTargetType.USER, Long.valueOf(orgId));
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();

        boolean isSysAdmin = judgeAdmin(spec, orgUser, currentAdmin);

        List<Long> allOrgTagIds = userManager.getValidOrgAndUserIds(Long.valueOf(orgId));
        allOrgTagIds.add(getOptTenantId()); // It is necessary to query the default policy
        spec.getCriteria().add(SearchCriteria.in("orgId", allOrgTagIds));
        spec.getCriteria().add(SearchCriteria.equal("isSysAdmin", isSysAdmin));
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));

        boolean ignoreAuthOrg = getIgnoreAuthOrg(spec);

        Page<AuthPolicy> policyPage = authUserAssocPolicyListRepo.find(
            spec.getCriteria(), pageable, AuthPolicy.class, ignoreAuthOrg ?
                AuthPolicyOrgConverter::objectArrToUserAssociatedAuthPolicy :
                AuthPolicyOrgConverter::objectArrToUserAssociatedAuthPolicyAndOrg, null);
        if (policyPage.hasContent()) {
          setOrgName(policyPage);
        }
        return policyPage;
      }
    }.execute();
  }

  /**
   * Query the policies that the current authorizer does not authorize to users.
   */
  @Override
  public Page<AuthPolicy> userUnauthPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {

      @Override
      protected Page<AuthPolicy> process() {
        GenericSpecification<AuthPolicy> specCopy = new GenericSpecification<>(spec.getCriteria());

        // Query the policies that have been authorized to users
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));
        Page<AuthPolicy> authorizedPolicies = userAssociatedPolicyList(spec,
            PageRequest.of(0, 5000, JpaSort.by(Order.asc("id"))), false);

        // Switch the authorization scope to all permissions of the current user
        checkSwitchUnAuthOrgCondition(specCopy);

        // Query the policies that not authorized to users
        if (authorizedPolicies.hasContent()) {
          specCopy.getCriteria().add(SearchCriteria.notIn("id",
              authorizedPolicies.getContent().stream().map(AuthPolicy::getId)
                  .collect(Collectors.toSet())));
        }
        return userAssociatedPolicyList(specCopy, pageable, true);
        // return userPolicyList(specificationCopy, pageable);
      }
    }.execute();
  }

  @Override
  public List<App> userAppList(Long userId) {
    return new BizTemplate<List<App>>() {

      @Override
      protected void checkParams() {
        // Check user existed
        if (!getUserId().equals(userId)) {
          userManager.checkOrgAndFind(OrgTargetType.USER, userId);
        }
      }

      @Override
      protected List<App> process() {
        Long tenantId = getOptTenantId();
        List<Long> authAppIds = isSysAdmin() ?
            authPolicyOrgRepo.findAuthAppIdsOfSysAdminUser(tenantId, List.of(userId, tenantId)) :
            authPolicyOrgRepo.findAuthAppIdsOfNonSysAdminUser(tenantId, List.of(userId, tenantId));
        if (isEmpty(authAppIds)) {
          return Collections.emptyList();
        }
        return appQuery.checkAndFind(authAppIds, true);
      }
    }.execute();
  }

  @Override
  public App userAppFuncList(Long userId, String appIdOrCode, Boolean joinApi,
      Boolean joinTag, Boolean onlyEnabled) {
    return new BizTemplate<App>(false) {
      final Long tenantId = getOptTenantId();
      App appDb;
      User userDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        if (!getUserId().equals(userId)) {
          userDb = (User) userManager.checkOrgAndFind(OrgTargetType.USER, userId);
        }

        // Check the application existed
        appDb = appQuery.checkAndFind(appIdOrCode);

        // NOOP:: Check app opened <- Condition in findAuthPolicyIdsOfNonSysAdminUser()
      }

      @Override
      protected App process() {
        // Join application apis
        if (joinApi) {
          appFuncQuery.setApis(appDb);
        }

        // Query all functions of application or _ADMIN policy
        Long appId = appDb.getId();
        // Note: Returning disabled is mandatory
        // The front-end needs to display disabled data copywriting
        List<AppFunc> appAllFuncs = appFuncQuery.findAllByAppId(appId, onlyEnabled);
        // Join function apis
        if (joinApi) {
          appFuncQuery.setApis(appAllFuncs);
        }
        // Join function tags
        if (joinTag) {
          appFuncQuery.setTags(appAllFuncs);
        }

        // Operation and application administrator queries all application functions.
        boolean isAdmin = judgeIsAdminAppFunc(appDb, userDb);
        if (isAdmin) {
          if (joinTag) {
            List<AppFunc> appFunc = appAllFuncs.stream()
                .filter(x -> x.hasTags(applicationInfo.getEditionType()))
                .map(x -> x.setHasAuth(true)).collect(Collectors.toList());
            return appDb.setAppFunc(appFunc);
          }
          return appDb.setAppFunc(appAllFuncs.stream().map(x -> x.setHasAuth(true))
              .collect(Collectors.toList()));
        }

        // Query the authorized functions of non admin user
        List<Long> allOrgIds = userManager.getValidOrgAndUserIds(userId);
        allOrgIds.add(tenantId);
        List<Long> authPolicyIds = authPolicyOrgRepo.findAuthPolicyIdsOfNonSysAdminUser(
            appId, tenantId, allOrgIds);

        Map<Long, AppFunc> appHasAuthFuncsMap = isEmpty(authPolicyIds)
            ? Collections.emptyMap() : appFuncQuery.findValidByPolicyIds(authPolicyIds)
            .stream().collect(Collectors.toMap(AppFunc::getId, x -> x));

        if (joinTag) {
          return appDb.setAppFunc(appAllFuncs.stream()
              .filter(x -> x.hasTags(applicationInfo.getEditionType()))
              .map(x -> x.setHasAuth(appHasAuthFuncsMap.containsKey(x.getId())))
              .collect(Collectors.toList()));
        } else {
          return appDb.setAppFunc(appAllFuncs.stream()
              .map(x -> x.setHasAuth(appHasAuthFuncsMap.containsKey(x.getId())))
              .collect(Collectors.toList()));
        }
      }
    }.execute();
  }

  /**
   * Switch to query the current user's authorization policies
   */
  @Override
  public void checkSwitchUnAuthOrgCondition(GenericSpecification<AuthPolicy> spec) {
    findFirstValueAndRemove(spec.getCriteria(), "orgId", EQUAL);
    findFirstValueAndRemove(spec.getCriteria(), "orgType", EQUAL);

    spec.getCriteria().add(SearchCriteria.equal("orgId", getUserId()));
    spec.getCriteria().add(SearchCriteria.equal("orgType", USER.getValue()));
  }

  public boolean judgeAdmin(GenericSpecification<AuthPolicy> spec, User user,
      boolean currentAdmin) {
    // Query the all policy of all application when the system administrator queries
    boolean isSysAdmin = currentAdmin || isNull(user) // orgUser is current user
        ? isSysAdmin() || hasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PLATFORM_ADMIN)
        : user.getSysAdmin();
    String appId = findFirstValue(spec.getCriteria(), "appId", EQUAL);
    if (!isSysAdmin && isNotEmpty(appId)) {
      // Query the all policy of an application when the application administrator queries
      AuthPolicy appAdminPolicy = authPolicyQuery.checkAppAdminAndFind(Long.parseLong(appId));
      if (currentAdmin) {
        isSysAdmin = hasPolicy(appAdminPolicy.getCode());
      } else {
        List<Long> allOrgIds = userManager.getValidOrgAndUserIds(user.getId());
        Long tenantId = getOptTenantId();
        allOrgIds.add(tenantId);
        List<IdAndCode> authPolicies = authPolicyOrgRepo
            .findAuthOfNonSysAdminUser(tenantId, getClientId(), allOrgIds);
        isSysAdmin = authPolicies.stream()
            .anyMatch(x -> x.getCode().equals(appAdminPolicy.getCode()));
      }
    }
    return isSysAdmin;
  }

  private void setOrgName(Page<AuthPolicy> authPolicyPage) {
    Map<Long, String> orgIdAndNames = userManager.getOrgNameByIds(
        authPolicyPage.getContent().stream().map(AuthPolicy::getOrgId).collect(Collectors.toSet()));
    for (AuthPolicy authPolicy : authPolicyPage.getContent()) {
      authPolicy.setOrgName(orgIdAndNames.get(authPolicy.getOrgId()));
    }
  }

  public static boolean getIgnoreAuthOrg(GenericSpecification<AuthPolicy> spec) {
    String ignoreAuthOrg = findFirstValue(spec.getCriteria(), "ignoreAuthOrg");
    return isNotEmpty(ignoreAuthOrg) && parseBoolean(ignoreAuthOrg);
  }

  private boolean judgeIsAdminAppFunc(App appDb, User userDb) {
    if (isNull(userDb)) {
      return isSysAdmin();
    }
    boolean isSysAdmin = hasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PLATFORM_ADMIN)
        || userDb.getSysAdmin();
    if (!isSysAdmin && nonNull(appDb)) {
      // Query the all policy of an application when the application administrator queries
      AuthPolicy appAdminPolicy = authPolicyQuery.checkAppAdminAndFind(appDb.getId());
      List<Long> allOrgIds = userManager.getValidOrgAndUserIds(userDb.getId());
      Long tenantId = getOptTenantId();
      allOrgIds.add(tenantId);
      List<IdAndCode> authPolicies = authPolicyOrgRepo
          .findAuthOfNonSysAdminUser(tenantId, getClientId(), allOrgIds);
      isSysAdmin = authPolicies.stream()
          .anyMatch(x -> x.getCode().equals(appAdminPolicy.getCode()));
    }
    return isSysAdmin;
  }
}
