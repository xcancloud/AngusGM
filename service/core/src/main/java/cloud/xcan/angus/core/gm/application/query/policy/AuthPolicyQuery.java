package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface AuthPolicyQuery {

  AuthPolicy detail(String idOrCode);

  Page<AuthPolicy> list(GenericSpecification<AuthPolicy> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<AuthPolicy> findTenantAppDefaultPolices(Long tenantId);

  List<AuthPolicy> findOperableTenantClientPolicies();

  List<AuthPolicy> findOperableOpClientPoliciesByAppId(Long appId);

  List<AuthPolicy> findByAppIdIn(Collection<Long> appIds);

  AuthPolicy checkAndFind(Long policyId, boolean checkEnabled, boolean checkOpenAppValid);

  AuthPolicy checkAndFindTenantPolicy(Long policyId, boolean checkEnabled,
      boolean checkOpenAppValid);

  List<AuthPolicy> checkAndFind(Collection<Long> policyIds, boolean checkEnabled,
      boolean checkOpenAppValid);

  List<AuthPolicy> checkAndFindTenantPolicy(Collection<Long> policyIds, boolean checkEnabled,
      boolean checkOpenAppValid);

  AuthPolicy checkAppAdminAndFind(Long appId);

  void checkPolicyQuota(Long tenantId, long incr);

  void checkOpPolicyPermission();

  boolean hasOpPolicyPermission();

  void checkOpPolicyPermission(Collection<AuthPolicy> policies);

  boolean hasOpPolicyPermission(Collection<AuthPolicy> policies);

  void checkAuthPolicyPermission(Long appId, Collection<Long> policyIds);

  void checkAuthPolicyPermission(Collection<Long> policyIds);

  void checkAuthPolicyPermission(List<AuthPolicy> policies);

  void checkAuthPolicyPermission(Long appId, Long userId, Collection<Long> policyIds);

  void checkDuplicateParam(List<AuthPolicy> policies, boolean add);

  void checkUniqueCodeAndNameSuffix(List<AuthPolicy> policies);

  void checkCodeSuffixRepeated(List<AuthPolicy> policies);

  void setAppInfo(List<AuthPolicy> policies);

}
