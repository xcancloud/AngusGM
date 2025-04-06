package cloud.xcan.angus.core.gm.domain.policy;

import static cloud.xcan.angus.api.commonlink.AASConstant.POLICY_PRE_DEFINED_ADMIN_SUFFIX;

import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AuthPolicyRepo extends BaseRepository<AuthPolicy, Long>,
    NameJoinRepository<AuthPolicy, Long> {

  @Override
  Page<AuthPolicy> findAll(Specification<AuthPolicy> spc, Pageable pageable);

  List<AuthPolicy> findAllByCodeIn(Collection<String> codes);

  Optional<AuthPolicy> findByCode(String code);

  List<AuthPolicy> findByNameIn(Collection<String> names);

  List<AuthPolicy> findAllByAppIdIn(Collection<Long> appIds);

  boolean existsByCodeAndIdNot(String code, Long id);

  boolean existsByAppIdAndNameAndIdNot(Long appId, String name, Long id);

  boolean existsByAppIdAndCode(Long appId, String code);

  @Query(value = "SELECT COUNT(*) FROM auth_policy WHERE app_id = ?1 AND type = ?2 AND code LIKE '%?3'", nativeQuery = true)
  boolean existsByAppIdAndTypeAndCodeSuffix(Long appId, PolicyType type, String suffix);

  boolean existsByAppIdAndName(Long appId, String name);

  long countByTenantId(Long tenantId);

  /**
   * App pre_defined auth_policy + tenant customization policy.
   */
  @Query(value =
      "SELECT DISTINCT p.* FROM auth_policy p WHERE /*p.tenant_id = ?1 AND */p.app_id IN ?2 AND p.default0 = 1 AND type = 'PRE_DEFINED' "
          + "UNION SELECT p.* FROM auth_policy p WHERE p.tenant_id = ?1 AND p.app_id IN ?2 AND p.default0 = 1 AND type = 'USER_DEFINED'",
      nativeQuery = true)
  List<AuthPolicy> findTenantAvailableDefaultPolices(Long tenantId, Collection<Long> openedAppIds);

  /**
   * To authorize other tenants, you must first authorize tenant 1.
   */
  @Query(value =
      "SELECT DISTINCT p.* FROM auth_policy p, app_open o WHERE p.app_id = o.app_id AND p.client_id = 'xcan_tp' AND p.type = 'PRE_DEFINED' AND p.enabled = 1", nativeQuery = true)
  List<AuthPolicy> findOpenableTenantClientPolicies();

  /**
   * Tenant client applications need to include.
   */
  @Query(value = "SELECT DISTINCT p.* FROM auth_policy p WHERE p.app_id = ?1 AND p.type = 'PRE_DEFINED' AND p.enabled = 1", nativeQuery = true)
  List<AuthPolicy> findOpenableOpClientPoliciesByAppId(Long appId);

  @Override
  List<AuthPolicy> findByIdIn(Collection<Long> ids);

  Page<AuthPolicy> findAllByIdIn(Collection<Long> ids, Pageable pageable);

  @Query(value = "select * from auth_policy p WHERE (p.tenant_id = ?1 AND p.name like ?2 AND p.global = 0) or (p.global = 1 AND p.name like ?2) order by p.created_date DESC",
      countQuery = "select count(*) from auth_policy p WHERE (p.tenant_id = ?1 AND p.name like ?2 AND p.global = 0) or (p.global = 1 AND p.name like ?2)",
      nativeQuery = true)
  Page<AuthPolicy> findAllPolicyByName(Long tenantId, String name, Pageable pageable);

  @Query(value = "select * from auth_policy p WHERE (p.tenant_id = ?1  and p.global = 0) or (p.global = 1 ) order by p.created_date DESC",
      countQuery = "select count(*) from auth_policy p WHERE (p.tenant_id = ?1 AND p.global = 0) or (p.global = 1)",
      nativeQuery = true)
  Page<AuthPolicy> findAllPolicy(Long tenantId, Pageable pageable);

  List<AuthPolicy> findAllByIdInAndDefault0(Collection<Long> ids, Boolean default0);

  List<AuthPolicy> findAllByTypeAndEnabled(PolicyType type, Boolean enabled);

  List<AuthPolicy> findAllByIdInAndEnabled(Collection<Long> ids, Boolean enabled);

  @Query(value =
      "SELECT * FROM auth_policy p WHERE (p.tenant_id= ?1 AND p.global=0 AND p.type='PRE_DEFINED' AND p.app_id IN ?2)"
          + " OR (p.global=1 AND p.type='PRE_DEFINED' AND p.app_id IN ?2) ORDER BY p.created_date DESC", nativeQuery = true)
  List<AuthPolicy> findAppPreDefinedPolicy(Long tenantId, Set<Long> appIds);

  @Query(value =
      "SELECT * FROM auth_policy p WHERE (p.tenant_id= ?1 AND p.global=0 AND p.type='PRE_DEFINED')"
          + " OR (p.global=1 AND p.type='PRE_DEFINED') ORDER BY p.created_date DESC", nativeQuery = true)
  List<AuthPolicy> findAllPreDefinedPolicy(Long tenantId);

  @Query(value = "SELECT * FROM auth_policy WHERE app_id = ?1 AND type = 'PRE_DEFINED' "
      + "AND code like '%" + POLICY_PRE_DEFINED_ADMIN_SUFFIX + "'", nativeQuery = true)
  Optional<AuthPolicy> findAppAdminByAppId(Long appId);

  int countAllByType(PolicyType type);

  @Modifying
  @Query(value = "DELETE FROM auth_policy WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
