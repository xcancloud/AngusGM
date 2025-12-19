package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.AuthTenantConverter.defaultInitToPolicyTenant;
import static cloud.xcan.angus.core.gm.application.converter.AuthTenantConverter.openGrantInitToPolicyTenant;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.APP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_DEFAULT_APP_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SET_DEFAULT_APP_POLICY;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOpenCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.converter.AppOpenConverter;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant authorization policy command operations.
 * </p>
 * <p>
 * Manages tenant-level authorization policies which include two main components:
 * </p>
 * <p>
 * 1. Default authorization granted to tenants during signup, automatically effective for system
 * administrators.
 * </p>
 * <p>
 * 2. User default access application policies set by tenant administrators, effective for all
 * tenant users. Manual setup required if not configured.
 * </p>
 * <p>
 * Supports both cloud service and operation client environments with appropriate initialization and
 * cleanup operations.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class AuthPolicyTenantCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements
    AuthPolicyTenantCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private AppQuery appQuery;
  @Resource
  private AppOpenQuery appOpenQuery;
  @Resource
  private AppOpenCmd appOpenCmd;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Sets the default authorization policy for a specific application.
   * </p>
   * <p>
   * Validates that the application and policy exist, ensures consistency, and creates or updates
   * the default policy association for the tenant.
   * </p>
   * <p>
   * Only allows setting predefined and user-defined policies that belong to the current tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void defaultPolicySet(Long appId, Long policyId) {
    new BizTemplate<Void>(false) {
      App appDb;
      AuthPolicy policyDb;
      final Long tenantId = getTenantId();

      @Override
      protected void checkParams() {
        // Verify application exists
        appDb = appQuery.checkAndFind(appId.toString());
        // Verify policy exists and is accessible
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Ensure application and policy are consistent
        assertTrue(appId.equals(policyDb.getAppId()),
            "The appId is inconsistent with the policyId");
        // Ensure only predefined and tenant-owned user-defined policies can be set
        assertResourceNotFound(policyDb.isPreDefined() ||
                (policyDb.isUserDefined() && getTenantId().equals(policyDb.getTenantId())),
            policyId, "AuthPolicy");
      }

      @Override
      protected Void process() {
        // Find existing default policy for the tenant
        List<AuthPolicyOrg> policyTenants = authPolicyOrgRepo.findByTenantIdAndDefault0(
            tenantId, true);
        boolean hasDefault = false;
        if (isNotEmpty(policyTenants)) {
          for (AuthPolicyOrg policyTenant : policyTenants) {
            if (policyTenant.getAppId().equals(appId)) {
              // Update existing default policy
              policyTenant.setPolicyId(policyId).setPolicyType(policyDb.getType());
              authPolicyOrgRepo.save(policyTenant);
              hasDefault = true;
              break;
            }
          }
        }
        if (!hasDefault) {
          // Create new default policy association
          add0(singletonList(defaultInitToPolicyTenant(tenantId, policyDb)));
        }

        operationLogCmd.add(APP, appDb, SET_DEFAULT_APP_POLICY, policyDb.getName());
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes the default authorization policy for a specific application.
   * </p>
   * <p>
   * Prevents new users from accessing the application by removing the default policy association
   * for the tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void defaultPolicyDelete(Long appId) {
    new BizTemplate<Void>(false) {
      App appDb;

      @Override
      protected void checkParams() {
        // Verify application exists
        appDb = appQuery.checkAndFind(appId, false);
      }

      @Override
      protected Void process() {
        defaultPolicyDelete0(getOptTenantId(), appId);

        operationLogCmd.add(APP, appDb, DELETE_DEFAULT_APP_POLICY);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes the default authorization policy for a specific application and tenant.
   * </p>
   * <p>
   * Internal method used to prevent new users from accessing the application by removing the
   * default policy association.
   * </p>
   */
  @Override
  public void defaultPolicyDelete0(Long tenantId, Long appId) {
    authPolicyOrgRepo.deleteByTenantIdAndAppIdAndDefault(tenantId, appId, true);
  }

  /**
   * <p>
   * Initializes applications and policies for a tenant during signup.
   * </p>
   * <p>
   * For base applications: 1. Authorizes _ADMIN policy to system administrator (opening
   * authorization) 2. Authorizes _GUEST policy as default permission to all tenant users (default
   * authorization)
   * </p>
   * <p>
   * For cloud applications: 1. Authorizes _ADMIN policy to system administrator (opening
   * authorization) 2. Authorizes _USER policy as default permission to all tenant users (default
   * authorization)
   * </p>
   */
  @Override
  public void intAndOpenAppByTenantWhenSignup(Long tenantId) {
    if (!isCloudServiceEdition()) {
      return;
    }

    // Query policies that can be opened on tenant client
    List<AuthPolicy> policies = authPolicyQuery.findOperableTenantClientPolicies();
    if (isEmpty(policies)) {
      return;
    }

    List<App> apps = appQuery.checkAndFind(policies.stream().map(AuthPolicy::getAppId)
        .collect(Collectors.toSet()), false);
    if (isEmpty(apps)) {
      log.error("Initial open policy application not found");
      return;
    }

    // Open applications for tenant
    Set<Long> openedAppIds = appOpenQuery.findOpenedAppByTenantId(tenantId).stream()
        .map(AppOpen::getAppId).collect(Collectors.toSet());
    for (App app : apps) {
      if (!openedAppIds.contains(app.getId())) {
        appOpenCmd.open0(AppOpenConverter.toAppOpen(app, tenantId), app);
      }
    }

    // Authorize opened applications to tenant
    for (App app : apps) {
      if (app.isTenantApp()) {
        // Opening authorization: Authorize _ADMIN policy to system administrator
        openAdminAuth(tenantId, policies, app);

        // Default authorization
        if (app.isBaseApp()) {
          // Authorize _GUEST policy as default permission to all tenant users
          defaultGuestOrUserAuth(tenantId, app, policies.stream()
              .filter(x -> x.isGuestPolicy() && x.getAppId().equals(app.getId())));
        } else if (app.isCloudApp()) {
          // Authorize _USER policy as default permission to all tenant users
          defaultGuestOrUserAuth(tenantId, app, policies.stream()
              .filter(x -> x.isUserPolicy() && x.getAppId().equals(app.getId())));
        }
      }
    }
  }

  /**
   * <p>
   * Initializes applications and policies for a specific tenant and application.
   * </p>
   * <p>
   * Performs opening authorization for administrator and default authorization for tenant users
   * based on application type.
   * </p>
   * <p>
   * Operation client applications require manual authorization for default policies.
   * </p>
   */
  @Override
  public void intAppAndPolicyByTenantAndApp(Long tenantId, App app) {
    // Query policies that can be opened on operation client
    List<AuthPolicy> policies = authPolicyQuery.findOperableOpClientPoliciesByAppId(app.getId());
    if (isEmpty(policies)) {
      return;
    }

    // Opening authorization: Authorize _ADMIN policy to system administrator
    openAdminAuth(tenantId, policies, app);

    // Default authorization for tenant applications
    if (app.isTenantApp()) {
      if (app.isBaseApp()) {
        // Authorize _GUEST policy as default permission to all tenant users
        defaultGuestOrUserAuth(tenantId, app, policies.stream()
            .filter(x -> x.isGuestPolicy() && x.getAppId().equals(app.getId())));
      } else if (app.isCloudApp()) {
        // Authorize _USER policy as default permission to all tenant users
        defaultGuestOrUserAuth(tenantId, app, policies.stream()
            .filter(x -> x.isUserPolicy() && x.getAppId().equals(app.getId())));
      } else if (app.isOpApp()) {
        // NOOP:: Operation applications are only initialized for administrators
      }
    }
  }

  /**
   * <p>
   * Cancels application opening policies for a tenant.
   * </p>
   * <p>
   * Note: This method is intentionally left empty as canceling subscriptions and application
   * expiration requires complex policy invalidation and user-defined policy reference cleanup. All
   * policy authorizations are retained, and a global verification method @CheckAppNotExpired is
   * provided.
   * </p>
   */
  @Override
  public void appOpenPolicyCancel(Long tenantId, Long appId) {
    // NOOP:: Canceling subscriptions and application expiration requires invalid subscription policies
    // and user-defined policy references and this process is complex.
    // Therefore, all policy authorizations are retained, and a global verification application opening annotation @CheckAppNotExpired method is provided.
  }

  /**
   * <p>
   * Adds tenant authorization policies to the database.
   * </p>
   * <p>
   * Internal method used by other operations to batch create tenant-policy associations.
   * </p>
   */
  @Override
  public void add0(List<AuthPolicyOrg> authTenantPolicies) {
    if (isNotEmpty(authTenantPolicies)) {
      batchInsert(authTenantPolicies);
    }
  }

  /**
   * <p>
   * Sets default guest or user authorization for a tenant application.
   * </p>
   * <p>
   * Creates default authorization when it doesn't exist, ensuring all tenant users have appropriate
   * access permissions.
   * </p>
   */
  private void defaultGuestOrUserAuth(Long tenantId, App app, Stream<AuthPolicy> authPolicyStream) {
    AuthPolicy guestPolicy = authPolicyStream.findFirst().orElse(null);
    if (nonNull(guestPolicy)) {
      AuthPolicyOrg defaultAuth = authPolicyOrgRepo
          .findDefaultAuthByAppIdAndTenantId(app.getId(), tenantId).orElse(null);
      if (isNull(defaultAuth)) {
        defaultAuth = defaultInitToPolicyTenant(tenantId, guestPolicy);
        add0(singletonList(defaultAuth));
      } else {
        log.warn(String.format("Application %s default authorization already existed",
            app.getId()));
      }
    } else {
      log.error(String.format("Application %s default(_GUEST or _USER) policy not found",
          app.getId()));
    }
  }

  /**
   * <p>
   * Sets opening authorization for administrator on a tenant application.
   * </p>
   * <p>
   * Creates administrator authorization when it doesn't exist, ensuring system administrators have
   * appropriate access permissions.
   * </p>
   */
  private void openAdminAuth(Long tenantId, List<AuthPolicy> policies, App app) {
    AuthPolicy adminPolicy = policies.stream().filter(x -> x.isAdminPolicy()
        && x.getAppId().equals(app.getId())).findFirst().orElse(null);
    if (nonNull(adminPolicy)) {
      Optional<AuthPolicyOrg> adminAuth = authPolicyOrgRepo
          .findAdminOpenAuthByAppIdAndTenantId(app.getId(), tenantId);
      if (adminAuth.isEmpty()) {
        AuthPolicyOrg openAuth = openGrantInitToPolicyTenant(tenantId, adminPolicy);
        add0(singletonList(openAuth));
      } else {
        log.warn(String.format("Application %s administrator authorization already existed",
            app.getId()));
      }
    } else {
      log.error(String.format("Application %s administrator(_ADMIN) policy not found",
          app.getId()));
    }
  }

  @Override
  protected BaseRepository<AuthPolicyOrg, Long> getRepository() {
    return this.authPolicyOrgRepo;
  }
}
