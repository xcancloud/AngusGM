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
import cloud.xcan.angus.core.biz.Biz;
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


@Biz
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
   * Query user-defined and platform preset policies.
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

  @Override
  public List<AuthPolicy> findTenantAppDefaultPolices(Long tenantId) {
    // closeMultiTenantCtrl(); <- For query PRE_DEFINED policies
    List<Long> openAppIds = isCloudServiceEdition()
        /* Fix:: Excluding private editions applications and authorizations for cloud service edition */
        ? appOpenQuery.findValidAppIdsByTenantIdAndEditionType(tenantId, EditionType.CLOUD_SERVICE)
        : appOpenQuery.findValidAppIdsByTenantId(tenantId);
    return isEmpty(openAppIds) ? Collections.emptyList()
        : authPolicyRepo.findTenantAvailableDefaultPolices(tenantId, openAppIds);
  }

  /**
   * Query the policies that can be opened on the tenant client.
   */
  @Override
  public List<AuthPolicy> findOperableTenantClientPolicies() {
    return authPolicyRepo.findOpenableTenantClientPolicies();
  }

  @Override
  public List<AuthPolicy> findByAppIdIn(Collection<Long> appIds) {
    return authPolicyRepo.findAllByAppIdIn(appIds);
  }

  /**
   * Query the policies that can be opened on the operation client and tenant client applications
   * need to include.
   */
  @Override
  public List<AuthPolicy> findOperableOpClientPoliciesByAppId(Long appId) {
    return authPolicyRepo.findOpenableOpClientPoliciesByAppId(appId);
  }

  /**
   * Important:: Consider turning off multi tenant control when you need to check predefined
   * policies.
   */
  @Override
  public AuthPolicy checkAndFind(Long policyId, boolean checkEnabled, boolean checkOpenAppValid) {
    return checkAndFind(List.of(policyId), checkEnabled, checkOpenAppValid).get(0);
  }

  @Override
  public AuthPolicy checkAndFindTenantPolicy(Long policyId, boolean checkEnabled,
      boolean checkOpenAppValid) {
    return checkAndFindTenantPolicy(List.of(policyId), checkEnabled, checkOpenAppValid).get(0);
  }

  /**
   * Important:: Consider turning off multi tenant control when you need to check predefined
   * policies.
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

  @Override
  public AuthPolicy checkAppAdminAndFind(Long appId) {
    return authPolicyRepo.findAppAdminByAppId(appId).orElseThrow(
        () -> ResourceNotFound.of(String.format("App administrator %s not found", appId)));
  }

  @Override
  public void checkPolicyQuota(Long tenantId, long incr) {
    if (incr > 0) {
      long num = authPolicyRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.PolicyCustom, null, num + incr);
    }
  }

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

  @Override
  public void checkOpPolicyPermission() {
    if (isOpSysAdmin()) {
      return;
    }
    checkHasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PERMISSION_USER, OP_PLATFORM_ADMIN);
  }

  @Override
  public boolean hasOpPolicyPermission() {
    if (isOpSysAdmin()) {
      return true;
    }
    return hasAnyPolicy(TOP_PERMISSION_ADMIN, OP_PERMISSION_USER, OP_PLATFORM_ADMIN);
  }

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
   * Authorizer must have authorization policy permission.
   *
   * @param appId     Authorized application id
   * @param policyIds Authorized policy ids
   */
  @Override
  public void checkAuthPolicyPermission(Long appId, Collection<Long> policyIds) {
    checkAuthPolicyPermission(appId, getUserId(), policyIds);
  }

  /**
   * Authorizer must have authorization policy permission.
   *
   * @param policyIds Authorized policy ids
   */
  @Override
  public void checkAuthPolicyPermission(Collection<Long> policyIds) {
    List<AuthPolicy> policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, false, false);
    checkAuthPolicyPermission(policyDb);
  }

  /**
   * Authorizer must have authorization policy permission.
   *
   * @param policies Authorized policies
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
   * Authorizer must have authorization policy permission.
   *
   * @param appId     Authorized application id
   * @param userId    Authorized user ids
   * @param policyIds Authorized policy ids
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
    // Check code duplicate in param
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
   * The predefined policy code {@link AuthConstant#POLICY_PRE_DEFINED_SUFFIX} cannot be repeated.
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
