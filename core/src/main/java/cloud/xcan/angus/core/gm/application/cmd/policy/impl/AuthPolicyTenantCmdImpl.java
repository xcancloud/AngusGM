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
import cloud.xcan.angus.core.biz.Biz;
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
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
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
 * Tenant authorization(AuthOrgType=TENANT) includes two parts in table auth_policy_org:
 * <p>
 * 1. It is authorized to the tenant when signup, and the system administrator takes effect by
 * default;
 * <p>
 * 2. The tenant administrator sets the user default access application policy, which takes effect
 * for all tenant users. If it is not set, it needs to be set manually, otherwise access will be
 * disabled.
 *
 * @author XiaoLong Liu
 */
@Biz
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void defaultPolicySet(Long appId, Long policyId) {
    new BizTemplate<Void>(false) {
      App appDb;
      AuthPolicy policyDb;
      final Long tenantId = getTenantId();

      @Override
      protected void checkParams() {
        // Check the application existed
        appDb = appQuery.checkAndFind(appId.toString());
        // Check the policy existed, preset policies need to be considered -> closeMultiTenantCtrl();
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Check the app is consistent with the policy
        assertTrue(appId.equals(policyDb.getAppId()),
            "The appId is inconsistent with the policyId");
        // Check can only query preset and user-defined policies
        assertResourceNotFound(policyDb.isPreDefined() ||
                (policyDb.isUserDefined() && getTenantId().equals(policyDb.getTenantId())),
            policyId, "AuthPolicy");
      }

      @Override
      protected Void process() {
        List<AuthPolicyOrg> policyTenants = authPolicyOrgRepo.findByTenantIdAndDefault0(
            tenantId, true);
        boolean hasDefault = false;
        if (isNotEmpty(policyTenants)) {
          for (AuthPolicyOrg policyTenant : policyTenants) {
            if (policyTenant.getAppId().equals(appId)) {
              // Modify default policy id
              policyTenant.setPolicyId(policyId).setPolicyType(policyDb.getType());
              authPolicyOrgRepo.save(policyTenant);
              hasDefault = true;
              break;
            }
          }
        }
        if (!hasDefault) {
          add0(singletonList(defaultInitToPolicyTenant(tenantId, policyDb)));
        }

        operationLogCmd.add(APP, appDb, SET_DEFAULT_APP_POLICY, policyDb.getName());
        return null;
      }
    }.execute();
  }

  /**
   * Prevent new users from accessing the app.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void defaultPolicyDelete(Long appId) {
    new BizTemplate<Void>(false) {
      App appDb;

      @Override
      protected void checkParams() {
        // Check the application existed -> closeMultiTenantCtrl();
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
   * Prevent new users from accessing the app.
   */
  @Override
  public void defaultPolicyDelete0(Long tenantId, Long appId) {
    authPolicyOrgRepo.deleteByTenantIdAndAppIdAndDefault(tenantId, appId, true);
  }

  /**
   * <pre>
   * Application initialization when the tenant signup.
   *
   * Bass application:
   *  1. Authorize _ADMIN policy to system administrator (Opening authorization);
   *  2. Authorize _GUEST policy as default permission to all users of tenant (Default authorization).
   *
   * Cloud application:
   *  1. Authorize _ADMIN policy to system administrator (Opening authorization);
   *  2. Authorize _USER policy as default permission to all users of tenant (Default authorization).
   * </pre>
   */
  @Override
  public void intAndOpenAppByTenantWhenSignup(Long tenantId) {
    if (!isCloudServiceEdition()) {
      return;
    }

    // closeMultiTenantCtrl(); -> Should be called
    // Query the policies that can be opened on the tenant client.
    List<AuthPolicy> policies = authPolicyQuery.findOpenableTenantClientPolicies();
    if (isEmpty(policies)) {
      return;
    }

    List<App> apps = appQuery.checkAndFind(policies.stream().map(AuthPolicy::getAppId)
        .collect(Collectors.toSet()), false);
    if (isEmpty(apps)) {
      log.error("Initial open policy application not found");
      return;
    }

    // Opening applications for tenant
    Set<Long> openedAppIds = appOpenQuery.findOpenedAppByTenantId(tenantId).stream()
        .map(AppOpen::getAppId).collect(Collectors.toSet());
    for (App app : apps) {
      if (!openedAppIds.contains(app.getId())) {
        appOpenCmd.open0(AppOpenConverter.toAppOpen(app, tenantId), app);
      }
    }

    // Authorize the opened application to tenant
    for (App app : apps) {
      if (app.isTenantApp()) {
        // Opening authorization: Authorize _ADMIN policy to system administrator
        openAdminAuth(tenantId, policies, app);

        // Default authorization
        if (app.isBaseApp()) {
          // Authorize _GUEST policy as default permission to all users of tenant
          defaultGuestOrUserAuth(tenantId, app, policies.stream()
              .filter(x -> x.isGuestPolicy() && x.getAppId().equals(app.getId())));
        } else if (app.isCloudApp()) {
          // Authorize _USER policy as default permission to all users of tenant
          defaultGuestOrUserAuth(tenantId, app, policies.stream()
              .filter(x -> x.isUserPolicy() && x.getAppId().equals(app.getId())));
        }
      }
    }
  }

  @Override
  public void intAppAndPolicyByTenantAndApp(Long tenantId, App app) {
    // Assert.assertTrue(PrincipalContext.isUserAction(), "Must be a user action");

    // NOOP:: Opening applications for tenant

    // Authorize the opened application to tenant
    List<AuthPolicy> policies = authPolicyQuery.findOpenableOpClientPoliciesByAppId(app.getId());
    if (isEmpty(policies)) {
      return;
    }

    // Opening authorization: Authorize _ADMIN policy to system administrator
    openAdminAuth(tenantId, policies, app);

    // Default authorization, The operation client needs manual authorization
    if (app.isTenantApp()) {
      if (app.isBaseApp()) {
        // Authorize _GUEST policy as default permission to all users of tenant
        defaultGuestOrUserAuth(tenantId, app, policies.stream()
            .filter(x -> x.isGuestPolicy() && x.getAppId().equals(app.getId())));
      } else if (app.isCloudApp()) {
        // Authorize _USER policy as default permission to all users of tenant
        defaultGuestOrUserAuth(tenantId, app, policies.stream()
            .filter(x -> x.isUserPolicy() && x.getAppId().equals(app.getId())));
      } else if (app.isOpApp()) {
        // NOOP:: The operation application is only initialized for the administrator
      }
    }
  }

  @Override
  public void appOpenPolicyCancel(Long tenantId, Long appId) {
    // NOOP:: Cancelling the subscription and application expiration requires invalid subscription policies
    // and user-defined policy references and this process is complex.
    // Therefore, all policy authorizations are retained, and a global verification application opening annotation @CheckAppNotExpired method is provided.
  }

  @Override
  public void add0(List<AuthPolicyOrg> authTenantPolicies) {
    if (isNotEmpty(authTenantPolicies)) {
      batchInsert(authTenantPolicies);
    }
  }

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
