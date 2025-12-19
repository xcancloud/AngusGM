package cloud.xcan.angus.core.gm.application.query.policy.impl;

import static cloud.xcan.angus.api.commonlink.AuthConstant.OP_PLATFORM_ADMIN;
import static cloud.xcan.angus.api.commonlink.AuthConstant.TOP_PERMISSION_ADMIN;
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

/**
 * <p>
 * Implementation of authentication policy user query operations.
 * </p>
 * <p>
 * Manages user-policy relationship queries, validation, and authorization management. Provides
 * comprehensive user-policy querying with authorization support.
 * </p>
 * <p>
 * Supports policy-user queries, user-policy queries, authorization management, application function
 * queries, and unauthorized policy queries for comprehensive user-policy administration.
 * </p>
 */
@org.springframework.stereotype.Service
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

  /**
   * <p>
   * Retrieves users associated with specific policy.
   * </p>
   * <p>
   * Queries users that are authorized by the specified policy. Validates policy existence and
   * handles multi-tenant control.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves users not associated with specific policy.
   * </p>
   * <p>
   * Queries users that are not authorized by the specified policy. Validates policy existence for
   * proper filtering.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves policies associated with specific user.
   * </p>
   * <p>
   * Queries policies that are authorized to the specified user. Validates organization parameters
   * and handles authorization filtering.
   * </p>
   */
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

        // Fix: Only query orgType=USER
        // List<Long> orgIds = userManager.getValidOrgAndUserIds(Long.valueOf(orgId));
        // orgIds.add(getOptTenantId());
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

  /**
   * <p>
   * Retrieves user associated policy list.
   * </p>
   * <p>
   * Queries policies associated with specific user with comprehensive organization validation.
   * Handles admin privileges and organization hierarchy.
   * </p>
   */
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
   * <p>
   * Retrieves policies not authorized to user.
   * </p>
   * <p>
   * Queries policies that the current authorizer does not authorize to user. Compares authorized
   * policies with all available policies to find unauthorized ones.
   * </p>
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

  /**
   * <p>
   * Retrieves user application list.
   * </p>
   * <p>
   * Returns applications that the specified user has access to. Validates user existence and
   * authorization permissions.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves user application function list.
   * </p>
   * <p>
   * Returns application functions available for the specified user and application. Handles admin
   * privileges and authorization filtering.
   * </p>
   */
  @Override
  public App userAppFuncList(Long userId, String appIdOrCode, Boolean joinApi,
      Boolean onlyEnabled) {
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

        // NOOP: Check app opened <- Condition in findAuthPolicyIdsOfNonSysAdminUser()
      }

      @Override
      protected App process() {
        // Query all functions of application or _ADMIN policy
        // Note: Returning disabled is mandatory
        // The front-end needs to display disabled data copywriting
        List<AppFunc> appAllFuncs = appFuncQuery.findAllByAppId(appDb.getId(), onlyEnabled);

        // Join function tags
        appFuncQuery.setTags(appAllFuncs);

        // Join function APIs
        if (joinApi) {
          appFuncQuery.setApis(appDb);
          appFuncQuery.setApis(appAllFuncs);
        }

        // Operation and application administrator queries all application functions.
        boolean isAdmin = judgeIsAdminAppFunc(appDb, userDb);
        if (isAdmin) {
          List<AppFunc> appFunc = appAllFuncs.stream()
              .filter(x -> x.hasTags(applicationInfo.getEditionType()))
              .map(x -> x.setHasAuth(true)).collect(Collectors.toList());
          return appDb.setAppFunc(appFunc);
        }

        // Query the authorized functions of non admin user
        List<Long> allOrgIds = userManager.getValidOrgAndUserIds(userId);
        allOrgIds.add(tenantId);
        List<Long> authPolicyIds = authPolicyOrgRepo.findAuthPolicyIdsOfNonSysAdminUser(
            appDb.getId(), tenantId, allOrgIds);

        Map<Long, AppFunc> appHasAuthFuncsMap = isEmpty(authPolicyIds)
            ? Collections.emptyMap() : appFuncQuery.findValidByPolicyIds(authPolicyIds)
            .stream().collect(Collectors.toMap(AppFunc::getId, x -> x));

        List<AppFunc> appFunc = appAllFuncs.stream()
            .filter(x -> x.hasTags(applicationInfo.getEditionType()))
            .map(x -> x.setHasAuth(appHasAuthFuncsMap.containsKey(x.getId()))).toList();
        return appDb.setAppFunc(appFunc);
      }
    }.execute();
  }

  /**
   * <p>
   * Switches to query current user's authorization policies.
   * </p>
   * <p>
   * Modifies specification to query policies for current user instead of specified organization.
   * </p>
   */
  @Override
  public void checkSwitchUnAuthOrgCondition(GenericSpecification<AuthPolicy> spec) {
    findFirstValueAndRemove(spec.getCriteria(), "orgId", EQUAL);
    findFirstValueAndRemove(spec.getCriteria(), "orgType", EQUAL);

    spec.getCriteria().add(SearchCriteria.equal("orgId", getUserId()));
    spec.getCriteria().add(SearchCriteria.equal("orgType", USER.getValue()));
  }

  /**
   * <p>
   * Judges if user has admin privileges.
   * </p>
   * <p>
   * Determines admin status based on system admin privileges and application admin policies.
   * </p>
   */
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

  /**
   * <p>
   * Sets organization names for policy page.
   * </p>
   * <p>
   * Loads organization names and associates with policy records for display.
   * </p>
   */
  private void setOrgName(Page<AuthPolicy> authPolicyPage) {
    Map<Long, String> orgIdAndNames = userManager.getOrgNameByIds(
        authPolicyPage.getContent().stream().map(AuthPolicy::getOrgId).collect(Collectors.toSet()));
    for (AuthPolicy authPolicy : authPolicyPage.getContent()) {
      authPolicy.setOrgName(orgIdAndNames.get(authPolicy.getOrgId()));
    }
  }

  /**
   * <p>
   * Gets ignore authorization organization flag from specification.
   * </p>
   * <p>
   * Extracts and parses ignoreAuthOrg parameter from search criteria.
   * </p>
   */
  public static boolean getIgnoreAuthOrg(GenericSpecification<AuthPolicy> spec) {
    String ignoreAuthOrg = findFirstValue(spec.getCriteria(), "ignoreAuthOrg");
    return isNotEmpty(ignoreAuthOrg) && parseBoolean(ignoreAuthOrg);
  }

  /**
   * <p>
   * Judges if user has admin privileges for application functions.
   * </p>
   * <p>
   * Determines admin status for application function access based on system and application admin
   * privileges.
   * </p>
   */
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
