package cloud.xcan.angus.core.gm.application.query.policy.impl;

import static cloud.xcan.angus.api.commonlink.AuthConstant.OP_PERMISSION_USER;
import static cloud.xcan.angus.api.commonlink.AuthConstant.OP_PLATFORM_ADMIN;
import static cloud.xcan.angus.api.commonlink.AuthConstant.POLICY_PRE_DEFINED_SUFFIX;
import static cloud.xcan.angus.api.commonlink.AuthConstant.TOP_PERMISSION_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.POLICY_IS_DISABLED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkHasAnyPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasAnyPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isMultiTenantCtrl;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_VALUE_DUPLICATE_T;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.NO_POLICY_PERMISSION_KEY;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.NO_POLICY_PERMISSION_T;
import static cloud.xcan.angus.remote.message.http.ResourceExisted.M.RESOURCE_ALREADY_EXISTS_T2;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyListRepo;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicySearchRepo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.Forbidden;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of authentication policy query operations.
 * </p>
 * <p>
 * Manages authentication policy retrieval, validation, and permission management. Provides
 * comprehensive policy querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports policy detail retrieval, paginated listing, permission validation, quota management, and
 * policy uniqueness checking for comprehensive policy administration.
 * </p>
 */
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "AuthPolicy", table = "auth_policy",
    groupByColumns = {"created_date", "type", "grant_stage"/*Only OP Client*/, "enabled"})
public class AuthPolicyQueryImpl implements AuthPolicyQuery {

  @Resource
  private AuthPolicyRepo authPolicyRepo;
  @Resource
  private AuthPolicyListRepo authPolicyListRepo;
  @Resource
  private AuthPolicySearchRepo authPolicySearchRepo;
  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private AppOpenQuery appOpenQuery;
  @Resource
  private AppQuery appQuery;
  @Resource
  private UserManager userManager;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves detailed policy information by ID or code.
   * </p>
   * <p>
   * Fetches complete policy record with tenant and application validation. Throws ResourceNotFound
   * exception if policy does not exist.
   * </p>
   */
  @Override
  public AuthPolicy detail(String idOrCode) {
    return new BizTemplate<AuthPolicy>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected AuthPolicy process() {
        AuthPolicy policyDb = null;
        if (NumberUtils.isDigits(idOrCode)) {
          policyDb = authPolicyRepo.findById(Long.valueOf(idOrCode)).orElse(null);
        }
        if (isNull(policyDb)) {
          policyDb = authPolicyRepo.findByCode(idOrCode).orElse(null);
        }

        assertResourceNotFound(policyDb, idOrCode, "AuthPolicy");

        // Check that non-operating tenants are not allowed to query user-defined policies of other tenants
        assertResourceNotFound(policyDb.isPreDefined()
            || policyDb.getTenantId().equals(tenantId), idOrCode, "AuthPolicy");

        // Check the opened application existed
        appOpenQuery.checkAndFind(policyDb.getAppId(), tenantId, false);
        return policyDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves policies with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Enriches results with application
   * information for comprehensive display.
   * </p>
   */
  @Override
  public Page<AuthPolicy> list(GenericSpecification<AuthPolicy> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<AuthPolicy>>(false) {
      @Override
      protected Page<AuthPolicy> process() {
        Page<AuthPolicy> page = fullTextSearch
            ? authPolicySearchRepo.find(spec.getCriteria(), pageable, AuthPolicy.class, match)
            : authPolicyListRepo.find(spec.getCriteria(), pageable, AuthPolicy.class, null);
        if (page.hasContent()) {
          setAppInfo(page.getContent());
        }
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves tenant application default policies.
   * </p>
   * <p>
   * Returns default policies for tenant's opened applications. Handles cloud service edition
   * filtering for private edition applications.
   * </p>
   */
  @Override
  public List<AuthPolicy> findTenantAppDefaultPolices(Long tenantId) {
    // closeMultiTenantCtrl(); <- For query PRE_DEFINED policies
    List<Long> openAppIds = isCloudServiceEdition()
        /* Fix: Excluding private editions applications and authorizations for cloud service edition */
        ? appOpenQuery.findValidAppIdsByTenantIdAndEditionType(tenantId, EditionType.CLOUD_SERVICE)
        : appOpenQuery.findValidAppIdsByTenantId(tenantId);
    return isEmpty(openAppIds) ? Collections.emptyList()
        : authPolicyRepo.findTenantAvailableDefaultPolices(tenantId, openAppIds);
  }

  /**
   * <p>
   * Retrieves operable tenant client policies.
   * </p>
   * <p>
   * Returns policies that can be opened on the tenant client.
   * </p>
   */
  @Override
  public List<AuthPolicy> findOperableTenantClientPolicies() {
    return authPolicyRepo.findOpenableTenantClientPolicies();
  }

  /**
   * <p>
   * Retrieves policies by application IDs.
   * </p>
   * <p>
   * Returns policies associated with the specified applications.
   * </p>
   */
  @Override
  public List<AuthPolicy> findByAppIdIn(Collection<Long> appIds) {
    return authPolicyRepo.findAllByAppIdIn(appIds);
  }

  /**
   * <p>
   * Retrieves operable operation client policies by application ID.
   * </p>
   * <p>
   * Returns policies that can be opened on operation client and tenant client applications.
   * </p>
   */
  @Override
  public List<AuthPolicy> findOperableOpClientPoliciesByAppId(Long appId) {
    return authPolicyRepo.findOpenableOpClientPoliciesByAppId(appId);
  }

  /**
   * <p>
   * Validates and retrieves policy by ID.
   * </p>
   * <p>
   * Important: Consider turning off multi tenant control when you need to check predefined
   * policies.
   * </p>
   */
  @Override
  public AuthPolicy checkAndFind(Long policyId, boolean checkEnabled, boolean checkOpenAppValid) {
    return checkAndFind(List.of(policyId), checkEnabled, checkOpenAppValid).get(0);
  }

  /**
   * <p>
   * Validates and retrieves tenant policy by ID.
   * </p>
   * <p>
   * Validates policy existence and tenant-specific access control.
   * </p>
   */
  @Override
  public AuthPolicy checkAndFindTenantPolicy(Long policyId, boolean checkEnabled,
      boolean checkOpenAppValid) {
    return checkAndFindTenantPolicy(List.of(policyId), checkEnabled, checkOpenAppValid).get(0);
  }

  /**
   * <p>
   * Validates and retrieves multiple policies by IDs.
   * </p>
   * <p>
   * Important: Consider turning off multi tenant control when you need to check predefined
   * policies.
   * </p>
   */
  @Override
  public List<AuthPolicy> checkAndFind(Collection<Long> policyIds, boolean checkEnabled,
      boolean checkOpenAppValid) {
    if (isEmpty(policyIds)) {
      return null;
    }
    List<AuthPolicy> policiesDb = authPolicyRepo.findByIdIn(policyIds);
    findAndCheck0(policyIds, policiesDb, checkEnabled, checkOpenAppValid);
    return policiesDb;
  }

  /**
   * <p>
   * Validates and retrieves multiple tenant policies by IDs.
   * </p>
   * <p>
   * Handles multi-tenant control for tenant-specific policy validation.
   * </p>
   */
  @Override
  public List<AuthPolicy> checkAndFindTenantPolicy(Collection<Long> policyIds, boolean checkEnabled,
      boolean checkOpenAppValid) {
    if (isEmpty(policyIds)) {
      return null;
    }

    // Turn off multi tenant control
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    setMultiTenantCtrl(false);
    List<AuthPolicy> policiesDb = authPolicyRepo.findByIdIn(policyIds);
    setMultiTenantCtrl(isMultiTenantCtrl);

    findAndCheck0(policyIds, policiesDb, checkEnabled, checkOpenAppValid);
    return policiesDb;
  }

  /**
   * <p>
   * Validates and retrieves application administrator policy.
   * </p>
   * <p>
   * Returns application administrator policy for the specified application. Throws ResourceNotFound
   * if application administrator not found.
   * </p>
   */
  @Override
  public AuthPolicy checkAppAdminAndFind(Long appId) {
    return authPolicyRepo.findAppAdminByAppId(appId).orElseThrow(
        () -> ResourceNotFound.of(String.format("App administrator %s not found", appId)));
  }

  /**
   * <p>
   * Validates policy quota for tenant.
   * </p>
   * <p>
   * Checks if adding policies would exceed tenant quota limits. Throws appropriate exception if
   * quota would be exceeded.
   * </p>
   */
  @Override
  public void checkPolicyQuota(Long tenantId, long incr) {
    if (incr > 0) {
      long num = authPolicyRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.PolicyCustom, null, num + incr);
    }
  }

  /**
   * <p>
   * Validates and checks policy parameters.
   * </p>
   * <p>
   * Performs comprehensive validation including application existence and policy status.
   * </p>
   */
  private void findAndCheck0(Collection<Long> policyIds, List<AuthPolicy> policiesDb,
      boolean checkEnabled, boolean checkOpenAppValid) {
    assertResourceNotFound(isNotEmpty(policiesDb), policyIds.iterator().next(), "AuthPolicy");

    // Check the opened application existed
    appOpenQuery.checkAndFind(policiesDb.stream().map(AuthPolicy::getAppId)
        .collect(Collectors.toList()), getOptTenantId(), checkOpenAppValid);

    if (policyIds.size() != policiesDb.size()) {
      for (AuthPolicy app : policiesDb) {
        assertResourceNotFound(policyIds.contains(app.getId()), app.getId(), "AuthPolicy");
      }
    }
    if (checkEnabled) {
      for (AuthPolicy policy : policiesDb) {
        assertTrue(policy.getEnabled(), POLICY_IS_DISABLED_T, new Object[]{policy.getName()});
      }
    }
  }

  /**
   * <p>
   * Validates operation policy permission.
   * </p>
   * <p>
   * Ensures current user has appropriate operation policy permissions. Throws Forbidden exception
   * if permission is insufficient.
   * </p>
   */
  @Override
  public void checkOpPolicyPermission() {
    if (isOpSysAdmin()) {
      return;
    }
    checkHasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PERMISSION_USER, OP_PLATFORM_ADMIN);
  }

  /**
   * <p>
   * Checks if user has operation policy permission.
   * </p>
   * <p>
   * Returns true if user has appropriate operation policy permissions.
   * </p>
   */
  @Override
  public boolean hasOpPolicyPermission() {
    if (isOpSysAdmin()) {
      return true;
    }
    return hasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PERMISSION_USER, OP_PLATFORM_ADMIN);
  }

  /**
   * <p>
   * Validates operation policy permission for specific policies.
   * </p>
   * <p>
   * Ensures current user has appropriate permissions for platform type policies.
   * </p>
   */
  @Override
  public void checkOpPolicyPermission(Collection<AuthPolicy> policies) {
    if (isOpSysAdmin() || isEmpty(policies)) {
      return;
    }
    List<AuthPolicy> platformTypePolicies = policies.stream()
        .filter(AuthPolicy::isPlatformType).collect(Collectors.toList());
    if (isNotEmpty(platformTypePolicies)) {
      checkHasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PERMISSION_USER, OP_PLATFORM_ADMIN);
    }
  }

  /**
   * <p>
   * Checks if user has operation policy permission for specific policies.
   * </p>
   * <p>
   * Returns true if user has appropriate permissions for platform type policies.
   * </p>
   */
  @Override
  public boolean hasOpPolicyPermission(Collection<AuthPolicy> policies) {
    if (isOpSysAdmin() || isEmpty(policies)) {
      return true;
    }
    List<AuthPolicy> platformTypePolicies = policies.stream()
        .filter(AuthPolicy::isPlatformType).collect(Collectors.toList());
    if (isNotEmpty(platformTypePolicies)) {
      return hasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PERMISSION_USER, OP_PLATFORM_ADMIN);
    }
    return false;
  }

  /**
   * <p>
   * Validates authorization policy permission.
   * </p>
   * <p>
   * Authorizer must have authorization policy permission.
   * </p>
   * <p>
   *
   * @param appId     Authorized application id
   * @param policyIds Authorized policy ids
   *                  </p>
   */
  @Override
  public void checkAuthPolicyPermission(Long appId, Collection<Long> policyIds) {
    checkAuthPolicyPermission(appId, getUserId(), policyIds);
  }

  /**
   * <p>
   * Validates authorization policy permission.
   * </p>
   * <p>
   * Authorizer must have authorization policy permission.
   * </p>
   * <p>
   *
   * @param policyIds Authorized policy ids
   *                  </p>
   */
  @Override
  public void checkAuthPolicyPermission(Collection<Long> policyIds) {
    List<AuthPolicy> policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, false, false);
    checkAuthPolicyPermission(policyDb);
  }

  /**
   * <p>
   * Validates authorization policy permission.
   * </p>
   * <p>
   * Authorizer must have authorization policy permission.
   * </p>
   * <p>
   *
   * @param policies Authorized policies
   *                 </p>
   */
  @Override
  public void checkAuthPolicyPermission(List<AuthPolicy> policies) {
    if (isNotEmpty(policies)) {
      Map<Long, List<AuthPolicy>> policyMap = policies.stream()
          .collect(Collectors.groupingBy(AuthPolicy::getAppId));
      for (Long appId : policyMap.keySet()) {
        // Check the policy permission
        checkAuthPolicyPermission(appId, getUserId(), policyMap.get(appId).stream()
            .map(AuthPolicy::getId).collect(Collectors.toSet()));
      }
    }
  }

  /**
   * <p>
   * Validates authorization policy permission.
   * </p>
   * <p>
   * Authorizer must have authorization policy permission.
   * </p>
   * <p>
   *
   * @param appId     Authorized application id
   * @param userId    Authorized user ids
   * @param policyIds Authorized policy ids
   *                  </p>
   */
  @Override
  public void checkAuthPolicyPermission(Long appId, Long userId, Collection<Long> policyIds) {
    long optTenantId = getOptTenantId();
    appOpenQuery.checkAndFind(appId, optTenantId, true);

    // Authorized other tenants must have operation permission
    if (!PrincipalContext.getTenantId().equals(getOptTenantId())) {
      checkOpPolicyPermission();
    }
    // The system administrator has all permissions
    if (isSysAdmin()) {
      return;
    }

    // Turn off multi tenant control
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    setMultiTenantCtrl(false);

    // The application administrator has all permissions for the application
    AuthPolicy adminPolicy = authPolicyQuery.checkAppAdminAndFind(appId);
    if (hasPolicy(adminPolicy.getCode())) {
      return;
    }

    // General users must have the same authorization policy permission.
    List<Long> orgIds = userManager.getValidOrgAndUserIds(userId);
    orgIds.add(optTenantId);

    List<Long> authorizedPolicyIds = authPolicyOrgRepo
        .findAuthPolicyIdsOfNonSysAdminUser(appId, optTenantId, orgIds);
    for (Long policyId : policyIds) {
      if (!authorizedPolicyIds.contains(policyId)) {
        throw Forbidden.of(NO_POLICY_PERMISSION_T, new Object[]{policyId},
            NO_POLICY_PERMISSION_KEY);
      }
    }
    setMultiTenantCtrl(isMultiTenantCtrl);
  }

  /**
   * <p>
   * Validates duplicate parameters in policy list.
   * </p>
   * <p>
   * Checks for duplicate codes and names within the provided policy list. Ensures only one
   * application policies can be added at a time.
   * </p>
   */
  @Override
  public void checkDuplicateParam(List<AuthPolicy> policies, boolean add) {
    // Check code duplicate in param
    List<AuthPolicy> duplicateCodePolicies = policies.stream()
        .filter(x -> nonNull(x.getCode()))
        .filter(duplicateByKey(AuthPolicy::getCode))
        .collect(Collectors.toList());
    assertTrue(isEmpty(duplicateCodePolicies), PARAM_VALUE_DUPLICATE_T,
        new Object[]{"code", isEmpty(duplicateCodePolicies) ? null :
            duplicateCodePolicies.get(0).getCode()});
    // Check name duplicate in param
    List<AuthPolicy> duplicateNamePolicies = policies.stream()
        .filter(x -> nonNull(x.getName()))
        .filter(duplicateByKey(AuthPolicy::getName))
        .collect(Collectors.toList());
    assertTrue(isEmpty(duplicateNamePolicies), PARAM_VALUE_DUPLICATE_T,
        new Object[]{"name", isEmpty(duplicateNamePolicies) ? null :
            duplicateNamePolicies.get(0).getName()});
    if (add) {
      // Check only one application policies can be added
      long appIdsNum = policies.stream().map(AuthPolicy::getAppId).distinct().count();
      assertTrue(appIdsNum == 1, "Only one application policies can be added");
    }
  }

  /**
   * <p>
   * Validates unique code and name suffix for policies.
   * </p>
   * <p>
   * Ensures policy codes and names are unique within the application. Handles both insert and
   * update scenarios.
   * </p>
   */
  @Override
  public void checkUniqueCodeAndNameSuffix(List<AuthPolicy> policies) {
    for (AuthPolicy policy : policies) {
      if (nonNull(policy.getId())) {
        // Update or replace
        if (isNotEmpty(policy.getName())) {
          assertResourceExisted(!authPolicyRepo.existsByAppIdAndNameAndIdNot(
                  policy.getAppId(), policy.getName(), policy.getId()), RESOURCE_ALREADY_EXISTS_T2,
              new Object[]{policy.getName()});
        }
      } else {
        // Insert
        if (isNotEmpty(policy.getCode())) {
          assertResourceExisted(!authPolicyRepo.existsByAppIdAndCode(
                  policy.getAppId(), policy.getCode()), RESOURCE_ALREADY_EXISTS_T2,
              new Object[]{policy.getCode()});
        }
        if (isNotEmpty(policy.getName())) {
          assertResourceExisted(!authPolicyRepo.existsByAppIdAndName(
                  policy.getAppId(), policy.getName()), RESOURCE_ALREADY_EXISTS_T2,
              new Object[]{policy.getName()});
        }
      }
    }

    checkCodeSuffixRepeated(policies);
  }

  /**
   * <p>
   * Validates predefined policy code suffix uniqueness.
   * </p>
   * <p>
   * The predefined policy code {@link AuthConstant#POLICY_PRE_DEFINED_SUFFIX} cannot be repeated.
   * </p>
   */
  @Override
  public void checkCodeSuffixRepeated(List<AuthPolicy> policies) {
    // Check code suffix duplicate in param
    Map<String, List<AuthPolicy>> policyCodeSuffixMap = policies.stream()
        .filter(x -> x.isPreDefined() && nonNull(
            x.getId()) /* Only add, modification code not allowed */
            && isNotEmpty(x.getCodeSuffix()))
        .collect(Collectors.groupingBy(x -> "_" + x.getCodeSuffix()));
    if (isNotEmpty(policyCodeSuffixMap)) {
      for (String suffix : policyCodeSuffixMap.keySet()) {
        if (POLICY_PRE_DEFINED_SUFFIX.contains(suffix)) {
          if (policyCodeSuffixMap.get(suffix).size() > 1) {
            throw ProtocolException
                .of(String.format("Predefined application policy suffix %s is repeated", suffix));
          } else {
            // Check code suffix duplicate in db
            boolean existedSuffix = authPolicyRepo.existsByAppIdAndTypeAndCodeSuffix(
                policyCodeSuffixMap.get(suffix).get(0).getAppId(), PolicyType.PRE_DEFINED,
                suffix);
            if (existedSuffix) {
              throw ProtocolException
                  .of(String.format("Predefined application policy suffix %s is repeated", suffix));
            }
          }
        }
      }
    }
  }

  /**
   * <p>
   * Sets application information for policy list.
   * </p>
   * <p>
   * Loads application details and associates with policies for complete information.
   * </p>
   */
  @Override
  public void setAppInfo(List<AuthPolicy> policies) {
    Map<Long, App> appMap = appQuery.findMapById(
        policies.stream().map(AuthPolicy::getAppId).collect(Collectors.toSet()));
    for (AuthPolicy policy : policies) {
      App app = appMap.get(policy.getAppId());
      if (nonNull(app)) {
        policy.setAppName(app.getName()).setAppVersion(app.getVersion())
            .setAppEditionType(app.getEditionType());
      }
    }
  }
}
