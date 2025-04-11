package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.api.commonlink.AASConstant.POLICY_PRE_DEFINED_SUFFIX;
import static cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage.SIGNUP_SUCCESS;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.AuthTenantConverter.defaultInitToPolicyTenant;
import static cloud.xcan.angus.core.gm.application.converter.AuthTenantConverter.openGrantInitToPolicyTenant;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.POLICY_PRE_SUFFIX_ERROR_T;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.join;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.open.AppOpenRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOpenCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyFuncCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.converter.AppOpenConverter;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFunc;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFuncRepo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.transaction.annotation.Transactional;


@Biz
@Slf4j
public class AuthPolicyCmdImpl extends CommCmd<AuthPolicy, Long> implements AuthPolicyCmd {

  @Resource
  private AuthPolicyRepo authPolicyRepo;

  @Resource
  private AuthPolicyFuncCmd authPolicyFuncCmd;

  @Resource
  private AuthPolicyFuncRepo authPolicyFuncRepo;

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AppOpenRepo appOpenRepo;

  @Resource
  private AppOpenCmd appOpenCmd;

  @Resource
  private AppQuery appQuery;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private AuthPolicyTenantCmd authPolicyTenantCmd;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public List<IdKey<Long, Object>> add(List<AuthPolicy> policies) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final List<AppFunc> appFuncDb = new ArrayList<>();

      @Override
      protected void checkParams() {
        // Check the custom policy suffix is secure
        for (AuthPolicy policy : policies) {
          assertTrue(
              !policy.isPreDefined() || POLICY_PRE_DEFINED_SUFFIX.contains(policy.getCodeSuffix()),
              POLICY_PRE_SUFFIX_ERROR_T, new Object[]{join(POLICY_PRE_DEFINED_SUFFIX, ",")});
        }

        // Check the code and name duplication in params
        authPolicyQuery.checkDuplicateParam(policies, true);
        // Check the code and name duplication in db
        authPolicyQuery.checkUniqueCodeAndNameSuffix(policies);

        // Check that only permission operation are allowed to add PRE_DEFINED policies on OP client
        List<AuthPolicy> platformTypePolicies = policies.stream().filter(AuthPolicy::isPlatformType)
            .collect(Collectors.toList());
        authPolicyQuery.checkOpPolicyPermission(platformTypePolicies);

        // Check the code and name duplication under the platform when the type is equal to PRE_DEFINED
        if (isNotEmpty(platformTypePolicies)) {
          closeMultiTenantCtrl();
          authPolicyQuery.checkUniqueCodeAndNameSuffix(platformTypePolicies);
          // Check the app existed, appId is required in params
          List<Long> appIds = platformTypePolicies.stream().map(AuthPolicy::getAppId)
              .distinct().collect(Collectors.toList());
          List<App> apps = appQuery.checkAndFindTenantApp(appIds, true);
          // Set app clientId to policy
          platformTypePolicies.forEach(x -> x.setClientId(apps.stream().filter(
              a -> a.getId().equals(x.getAppId())).findFirst().orElseThrow().getClientId()));
          enableMultiTenantCtrl();
        }

        // Check the code and name duplication under the tenant when the type is equal to USER_DEFINED
        List<AuthPolicy> tenantTypePolicies = policies.stream().filter(AuthPolicy::isTenantType)
            .collect(Collectors.toList());
        if (isNotEmpty(tenantTypePolicies)) {
          authPolicyQuery.checkUniqueCodeAndNameSuffix(platformTypePolicies);
          // Check the app existed, appId is required in params
          List<Long> appIds = tenantTypePolicies.stream().map(AuthPolicy::getAppId)
              .distinct().collect(Collectors.toList());
          List<App> apps = appQuery.checkAndFindTenantApp(appIds, true);
          // Set app clientId to policy
          for (AuthPolicy policy : tenantTypePolicies) {
            policy.setClientId(apps.stream().filter(a -> a.getId().equals(policy.getAppId()))
                .findFirst().orElseThrow().getClientId());
          }
        }

        // Check the app functions existed
        for (AuthPolicy policy : policies) {
          if (policy.hasFunc()) {
            appFuncDb.addAll(appFuncQuery.checkAndFindTenantAppFunc(policy.getAppId(),
                policy.getPolicyFunc().stream().map(AuthPolicyFunc::getFuncId)
                    .collect(Collectors.toSet()), true));
          }
        }

        // Check the tenant quota when the type is equal to USER_DEFINED
        if (isNotEmpty(tenantTypePolicies)) {
          authPolicyQuery.checkPolicyQuota(getTenantId(), tenantTypePolicies.size());
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // NOOP:: Set default and safe params <- AuthPolicyAssembler

        // Save policies
        List<IdKey<Long, Object>> idKeys = batchInsert(policies, "code");

        // Save functions of policies
        if (isNotEmpty(appFuncDb)) {
          authPolicyFuncCmd.add0(policies, appFuncDb);
        }

        // Save operation log
        operationLogCmd.addAll(POLICY, policies, CREATED);
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void update(List<AuthPolicy> policies) {
    new BizTemplate<Void>() {
      final List<AppFunc> appFuncDb = new ArrayList<>();

      @Override
      protected void checkParams() {
        // Check the policies existed
        List<AuthPolicy> authPoliciesDb = authPolicyQuery.checkAndFind(policies.stream()
            .map(AuthPolicy::getId).collect(Collectors.toList()), false, true);

        // Check the code and name duplication in params
        authPolicyQuery.checkDuplicateParam(policies, false);
        // Check the code and name duplication in db
        authPolicyQuery.checkUniqueCodeAndNameSuffix(policies);

        // Check that only permission operation are allowed to add OPEN_GRANT and PRE_DEFINED policies on OP client
        List<AuthPolicy> platformTypePolicies = policies.stream().filter(AuthPolicy::isPlatformType)
            .collect(Collectors.toList());
        authPolicyQuery.checkOpPolicyPermission(platformTypePolicies);

        // NOOP:: Check app existed, appId is not required and is immutable

        // Check the app functions existed, appId is not required and is immutable
        Map<Long, Long> authPolicyAppMap = authPoliciesDb.stream()
            .collect(Collectors.toMap(AuthPolicy::getId, AuthPolicy::getAppId));
        for (AuthPolicy policy : policies) {
          if (policy.hasFunc()) {
            appFuncDb.addAll(appFuncQuery.checkAndFindTenantAppFunc(
                authPolicyAppMap.get(policy.getId()),
                policy.getPolicyFunc().stream().map(AuthPolicyFunc::getFuncId)
                    .collect(Collectors.toSet()), true));
          }
        }
      }

      @Override
      protected Void process() {
        // NOOP:: Set default and safe params <- params is immutable

        // Save policies
        List<AuthPolicy> policiesDb = batchUpdateOrNotFound0(policies);

        // Replace functions of policies when the functions is not empty
        if (isNotEmpty(appFuncDb)) {
          authPolicyFuncCmd.replace0(policies, appFuncDb);
        }

        // Save operation log
        operationLogCmd.addAll(POLICY, policiesDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public List<IdKey<Long, Object>> replace(List<AuthPolicy> policies) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      List<AuthPolicy> replacePolicies;
      List<AuthPolicy> replacePoliciesDb;

      @Override
      protected void checkParams() {
        replacePolicies = policies.stream().filter(policy -> nonNull(policy.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(replacePolicies)) {
          // Check the updated policies existed
          replacePoliciesDb = authPolicyQuery.checkAndFind(replacePolicies.stream()
              .map(AuthPolicy::getId).collect(Collectors.toList()), false, true);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        List<AuthPolicy> addApps = policies.stream().filter(app -> isNull(app.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addApps)) {
          idKeys.addAll(add(addApps));
        }

        if (isNotEmpty(replacePolicies)) {
          Map<Long, AuthPolicy> appDbMap = replacePoliciesDb.stream()
              .collect(Collectors.toMap(AuthPolicy::getId, x -> x));
          // Do not replace source and enabled
          replacePoliciesDb = replacePolicies.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, appDbMap.get(x.getId()),
                  "code", "type", "grantStage", "appId", "clientId", "enabled"))
              .collect(Collectors.toList());
          update(replacePoliciesDb);

          idKeys.addAll(replacePoliciesDb.stream()
              .map(x -> IdKey.of(x.getId(), x.getName())).toList());

          // Save operation log
          operationLogCmd.addAll(POLICY, replacePoliciesDb, UPDATED);
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>(true, true) {
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Check that only permission operation are allowed to add OPEN_GRANT and PRE_DEFINED policies on OP client
        policiesDb = authPolicyRepo.findAllById(ids);
        authPolicyQuery.checkOpPolicyPermission(policiesDb);
      }

      @Override
      protected Void process() {
        // Open multiTenantAutoCtrlWhenOpClient is required, Need to delete other tenant data
        if (isNotEmpty(policiesDb)) {
          authPolicyFuncRepo.deleteByPolicyIdIn(ids);
          authPolicyOrgRepo.deleteByPolicyIdIn(ids);
          authPolicyRepo.deleteByIdIn(ids);

          // Save operation log
          operationLogCmd.addAll(POLICY, policiesDb, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void enabled(List<AuthPolicy> policies) {
    new BizTemplate<Void>() {
      List<AuthPolicy> policiesDb;
      List<Long> ids;

      @Override
      protected void checkParams() {
        // Check the policies existed
        ids = policies.stream().map(AuthPolicy::getId).collect(Collectors.toList());
        policiesDb = authPolicyQuery.checkAndFind(ids, false, true);

        // Check that only permission operation are allowed to enable or disable PRE_DEFINED policies on OP client
        authPolicyQuery.checkOpPolicyPermission(policiesDb);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(policies);

        // Save operation log
        operationLogCmd.addAll(POLICY, policiesDb.stream()
            .filter(AuthPolicy::getEnabled).toList(), ENABLED);
        operationLogCmd.addAll(POLICY, policiesDb.stream()
            .filter(x -> !x.getEnabled()).toList(), DISABLED);
        return null;
      }
    }.execute();
  }

  /**
   * Manually initialize the PRE_DEFINED authorization policy for the tenants and it is only used to
   * authorize new application permissions to existing tenants.
   * <p>
   * Ignored when repeated initialization.
   *
   * <pre>
   *   - If policy is _ADMIN policy , save authorization when _ADMIN policy does not exist.
   *   - If policy is _USER or _GUEST policy, save authorization when default policy does not exist.
   * </pre>
   * <p>
   * Automatically initialize policy when signup succeed, see
   * {@link AuthPolicyTenantCmd#intAndOpenAppByTenantWhenSignup(Long)}
   */
  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void initAndOpenAppByPolicy(Long policyId) {
    new BizTemplate<Void>(false) {
      AuthPolicy authPolicyDb;
      App appDb;

      @Override
      protected void checkParams() {
        // Check the policy existed
        authPolicyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Check the policy type must be PRE_DEFINED
        assertTrue(authPolicyDb.isPlatformType(),
            "Only PRE_DEFINED type authorization policy is supported");
        // Check the policy type must be PRE_DEFINED
        assertTrue(!authPolicyDb.isExtPolicy(),
            "Only non _EXT type authorization policy is supported");
        // Check the policy type must be PRE_DEFINED
        assertTrue(!authPolicyDb.getGrantStage().isManual(),
            String.format("Non automatic authorization policy grantStage[%s]",
                authPolicyDb.getGrantStage().getValue()));
        // Check and find policy app
        appDb = appQuery.checkAndFind(authPolicyDb.getAppId(), true);
        // Check only allow authorization to initialize tenant applications
        assertTrue(!appDb.isOpApp(),
            "Operation application authorization initialization is not supported. Please authorize manually after opening");
      }

      @DoInFuture("Optimize to pageable query, the number of tenants may be large")
      @Override
      protected Void process() {
        // Open the application when it is initialized for the first time and is not opened
        openAppWhenInitPolicy(authPolicyDb, appDb);

        // Query the tenants of unauthorized policy
        Set<Long> unauthTenantIds = null;
        switch (authPolicyDb.getGrantStage()) {
          case SIGNUP_SUCCESS:
            unauthTenantIds = authPolicyDb.isAdminPolicy() ?
                // Query the signup tenants of unauthorized admin policy
                authPolicyOrgRepo.findTenantIdByWhenSignupAndUnauth(policyId) :
                // Query the signup tenants of unauthorized default policy
                authPolicyOrgRepo.findTenantIdByWhenSignupAndUnauthDefault(authPolicyDb.getAppId());
            break;
          //case REAL_NAME_PASSED:
          // NOOP:: Cloud service edition applications need to be opened by default
          //  unauthTenantIds = authPolicyDb.isAdminPolicy() ?
          //      // Query the realname tenants of unauthorized admin policy
          //      authPolicyOrgRepo.findTenantIdByWhenRealnameAndUnauth(policyId) :
          //      // Query the realname tenants of unauthorized default policy
          //      authPolicyOrgRepo.findTenantIdByWhenRealnameAndUnauthDefault(
          //          authPolicyDb.getAppId());
          //  break;
          //case OPEN_SUCCESS:
          // NOOP:: Cloud service edition applications need to be opened by default
          // Query the opened tenants of unauthorized policy
          // unauthTenantIds = authPolicyOrgRepo.findTenantIdByWhenAppOpenedAndUnauth(policyId);
          // break;
          //  throw new RuntimeException("Authorized OPEN_SUCCESS policy is not supported");
          case MANUAL:
            // NOOP:: Manual authorization is not supported
          default:
            // NOOP
        }

        // Save tenant authorization
        if (isNotEmpty(unauthTenantIds)) {
          List<AuthPolicyOrg> authPolicyTenantAddList = unauthTenantIds.stream()
              .map(tenantId -> authPolicyDb.isAdminPolicy()
                  ? openGrantInitToPolicyTenant(tenantId, authPolicyDb)
                  : defaultInitToPolicyTenant(tenantId, authPolicyDb))
              .collect(Collectors.toList());
          authPolicyTenantCmd.add0(authPolicyTenantAddList);
        }
        return null;
      }
    }.execute();
  }

  private void openAppWhenInitPolicy(AuthPolicy authPolicyDb, App appDb) {
    List<Long> unopenTenantIds = null;
    if (authPolicyDb.getGrantStage().equals(SIGNUP_SUCCESS)) {
      unopenTenantIds = appOpenRepo.findUnopenSignupTenantIdByAppId(authPolicyDb.getAppId());
    }
    if (isNotEmpty(unopenTenantIds)) {
      appOpenCmd.open0(unopenTenantIds.stream().map(x -> AppOpenConverter.toAppOpen(appDb, x))
          .collect(Collectors.toList()), appDb);
    }
  }

  public static String genPolicyCode() {
    return RandomStringUtils.randomAlphanumeric(8).toUpperCase();
  }

  @Override
  protected BaseRepository<AuthPolicy, Long> getRepository() {
    return this.authPolicyRepo;
  }
}
