package cloud.xcan.angus.core.gm.domain.policy.org;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndCode;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AuthPolicyOrgRepo extends BaseRepository<AuthPolicyOrg, Long> {

  List<AuthPolicyOrg> findAllByPolicyId(Long policyId);

  List<AuthPolicyOrg> findByAppIdAndTenantId(Long appId, Long tenantId);

  List<AuthPolicyOrg> findByTenantIdAndDefault0(Long tenantId, boolean default0);

  @Query(value = "SELECT DISTINCT tenant_id FROM auth_policy_org WHERE app_id =?1 AND open_auth = 1", nativeQuery = true)
  List<Long> findOpenAuthTenantIdByAppId(Long appId);

  /**
   * Each application can only have one opened authorization
   */
  @Query(value = "SELECT * FROM auth_policy_org WHERE app_id = ?1 AND tenant_id = ?2 "
      + "AND open_auth =1 AND policy_type = 'PRE_DEFINED' AND org_type = 'TENANT' AND grant_scope = 'TENANT_SYS_ADMIN'", nativeQuery = true)
  Optional<AuthPolicyOrg> findAdminOpenAuthByAppIdAndTenantId(Long appId, Long tenantId);

  /**
   * Each application can only have one default authorization
   */
  @Query(value = "SELECT * FROM auth_policy_org WHERE app_id = ?1 AND tenant_id = ?2 "
      + "AND default0 =1 AND org_type = 'TENANT' AND grant_scope = 'TENANT_ALL_USER'", nativeQuery = true)
  Optional<AuthPolicyOrg> findDefaultAuthByAppIdAndTenantId(Long appId, Long tenantId);

  @Query(value = "SELECT DISTINCT tenant_id FROM auth_user WHERE tenant_id NOT IN "
      + "(SELECT DISTINCT tenant_id FROM auth_policy_org WHERE policy_id=?1)", nativeQuery = true)
  Set<Long> findTenantIdByWhenSignupAndUnauth(Long policyId);

  @Query(value = "SELECT DISTINCT tenant_id FROM auth_user WHERE tenant_id NOT IN "
      + "(SELECT DISTINCT tenant_id FROM auth_policy_org WHERE app_id = ?1 AND default0 =1 "
      + "AND org_type = 'TENANT' AND grant_scope = 'TENANT_ALL_USER')", nativeQuery = true)
  Set<Long> findTenantIdByWhenSignupAndUnauthDefault(Long appId);

  @Query(value =
      "SELECT DISTINCT tenant_id FROM auth_user WHERE tenant_real_name_status ='AUDITED' AND tenant_id NOT IN "
          + "(SELECT DISTINCT tenant_id FROM auth_policy_org WHERE policy_id=?1)", nativeQuery = true)
  Set<Long> findTenantIdByWhenRealnameAndUnauth(Long policyId);

  @Query(value =
      "SELECT DISTINCT tenant_id FROM auth_user WHERE tenant_real_name_status ='AUDITED' AND tenant_id NOT IN "
          + "(SELECT DISTINCT tenant_id FROM auth_policy_org WHERE app_id = ?1 AND default0 =1 AND org_type = 'TENANT' AND grant_scope = 'TENANT_ALL_USER')", nativeQuery = true)
  Set<Long> findTenantIdByWhenRealnameAndUnauthDefault(Long appId);

  @Query(value =
      "SELECT DISTINCT tenant_id FROM app_open WHERE expiration_deleted = 0 AND tenant_id NOT IN "
          + "(SELECT DISTINCT tenant_id FROM auth_policy_org WHERE policy_id=?1)", nativeQuery = true)
  Set<Long> findTenantIdByWhenAppOpenedAndUnauth(Long policyId);

  @Query(value = "SELECT org_id FROM auth_policy_org WHERE policy_id = ?1 AND org_type = ?2", nativeQuery = true)
  List<Long> finOrgIdsByPolicyIdAndOrgType(Long policyId, String orgType);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.app_id FROM auth_policy a WHERE a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?1 ) " /* Must be a opened application */
          + " AND a.enabled =1 AND a.id IN (SELECT po.policy_id FROM auth_policy_org po WHERE po.org_id IN ?2 AND (po.org_type = 'USER' OR (po.org_type = 'TENANT' AND (po.grant_scope = 'TENANT_ALL_USER' OR po.default0 = 1))))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<Long> findAuthAppIdsOfNonSysAdminUser(Long tenantId, Collection<Long> userIdAndTenantId);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.app_id FROM auth_policy a WHERE a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?1 ) " /* Must be a opened application */
          + " AND a.enabled =1 AND a.id IN (SELECT po.policy_id FROM auth_policy_org po WHERE po.org_id IN ?2 AND (po.org_type = 'USER' OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.grant_scope = 'TENANT_ALL_USER' OR po.open_auth =1))))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<Long> findAuthAppIdsOfSysAdminUser(Long tenantId, Collection<Long> userIdAndTenantId);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.id FROM auth_policy a WHERE a.app_id = ?1 AND a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?2 ) " /* Must be a opened application */
          + " AND a.enabled =1 AND a.id IN (SELECT po.policy_id FROM auth_policy_org po WHERE po.org_id IN ?3 AND (po.org_type <> 'TENANT' OR (po.org_type = 'TENANT' AND (po.grant_scope = 'TENANT_ALL_USER' OR po.default0 = 1))))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<Long> findAuthPolicyIdsOfNonSysAdminUser(Long appId, Long tenantId, Collection<Long> orgIds);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.id, a.code FROM auth_policy a WHERE a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?1 AND client_id = ?2) " /* Must be a opened application */
          + " AND a.enabled =1 AND a.id IN (SELECT po.policy_id FROM auth_policy_org po WHERE po.org_id IN ?3 AND (po.org_type <> 'TENANT' OR (po.org_type = 'TENANT' AND (po.grant_scope = 'TENANT_ALL_USER' OR po.default0 = 1))))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<IdAndCode> findAuthOfNonSysAdminUser(Long tenantId, String clientId,
      Collection<Long> orgIds);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.id policyId, po.org_id orgId, po.org_type orgType, po.default0 default0, po.created_date date FROM auth_policy a"
          + " INNER JOIN auth_policy_org po ON a.id = po.policy_id AND a.app_id = ?1 AND po.app_id = ?1"
          + " WHERE a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?2 ) AND a.enabled = 1 AND po.org_id IN (?3) "
          + "  AND ( po.org_type <> 'TENANT' OR ( po.org_type = 'TENANT' AND (po.grant_scope = 'TENANT_ALL_USER' OR po.default0 = 1)))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<AuthOrgPolicyP> findAuthOfNonSysAdminUser(Long appId, Long tenantId,
      Collection<Long> orgIds);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.id FROM auth_policy a WHERE a.app_id = ?1 AND a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?2 ) " /* Must be a opened application */
          + " AND a.enabled =1 AND a.id IN (SELECT po.policy_id FROM auth_policy_org po WHERE po.org_id IN ?3 AND (po.org_type <> 'TENANT' OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.grant_scope = 'TENANT_ALL_USER' OR po.open_auth =1))))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<Long> findAuthPolicyIdsOfSysAdminUser(Long appId, Long tenantId, Collection<Long> orgIds);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.id, a.code FROM auth_policy a WHERE a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?1 AND client_id = ?2) " /* Must be a opened application */
          + " AND a.enabled =1 AND a.id IN (SELECT po.policy_id FROM auth_policy_org po WHERE po.org_id IN ?3 AND (po.org_type <> 'TENANT' OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.grant_scope = 'TENANT_ALL_USER' OR po.open_auth =1))))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<IdAndCode> findAuthOfSysAdminUser(Long tenantId, String clientId, Collection<Long> orgIds);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT a.id policyId, po.org_id orgId, po.org_type orgType, po.default0 default0, po.created_date date FROM auth_policy a"
          + " INNER JOIN auth_policy_org po ON a.id = po.policy_id AND a.app_id = ?1 AND po.app_id = ?1"
          + " WHERE a.app_id IN ( SELECT app_id FROM app_open WHERE tenant_id = ?2 ) AND a.enabled = 1 AND po.org_id IN (?3) "
          + "  AND (po.org_type <> 'TENANT' OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.grant_scope = 'TENANT_ALL_USER' OR po.open_auth =1)))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<AuthOrgPolicyP> findAuthOfSysAdminUser(Long appId, Long tenantId, Collection<Long> orgIds);

  @Query(value = "SELECT DISTINCT a.policy_id FROM auth_policy_org a WHERE a.app_id = ?1 AND a.tenant_id = ?2 AND a.org_type = ?3 AND a.org_id = ?4", nativeQuery = true)
  List<Long> findAuthPolicyIdsOfDept(Long appId, Long tenantId, String orgType, Long orgId);

  @Query(value = "SELECT DISTINCT a.org_id FROM auth_policy_org a WHERE a.policy_id = ?1 AND a.org_type = ?2 AND a.org_id IN ?3", nativeQuery = true)
  List<Long> findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(Long policyId, String orgType,
      Collection<Long> orgIds);

  @Query(value = "SELECT DISTINCT a.policy_id FROM auth_policy_org a WHERE a.org_id = ?1 AND a.org_type = ?2 AND a.policy_id IN ?3", nativeQuery = true)
  List<Long> findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(Long orgId, String orgType,
      Collection<Long> policyIds);

  @Query(value = "SELECT COUNT(*) FROM auth_policy_org a WHERE a.app_id = ?1 AND a.tenant_id = ?2 AND (a.grant_scope = 'TENANT_ALL_USER' OR a.default0 = 1) LIMIT 1", nativeQuery = true)
  Long existsAuthAllUser(Long appId, Long tenantId);

  /**
   * Important:: Multi tenant control needs to be turned off, because preset policies need to be
   * included
   */
  @Query(value =
      "SELECT DISTINCT po.app_id FROM auth_policy_org po WHERE po.org_id IN ?1 AND (po.org_type <> 'TENANT' OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.grant_scope = 'TENANT_ALL_USER')))"/* Must be an authorized policy (including default)*/, nativeQuery = true)
  List<Long> findAuthAppIdsOfNonSysAdminUser(Collection<Long> orgIds);

  List<AuthPolicyOrg> findByAppIdAndOrgIdAndOrgType(Long appId, Long orgId, AuthOrgType orgType);

  List<AuthPolicyOrg> findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(Long appId,
      Collection<Long> orgIds, AuthOrgType orgType, Collection<Long> policyIds);

  @Query(value = "SELECT DISTINCT app_id FROM auth_policy_org WHERE tenant_id = ?1", nativeQuery = true)
  List<Long> findAuthAppIdsByTenantId(Long tenantId);

  @Query(value = "SELECT DISTINCT app_id FROM auth_policy_org WHERE tenant_id = ?1 AND org_id IN ?2", nativeQuery = true)
  List<Long> findAuthAppIdsByTenantIdAndOrgIdIn(Long tenantId, List<Long> orgsIds);

  @Query(value = "SELECT DISTINCT app_id FROM auth_policy_org WHERE tenant_id = ?1 AND org_id = ?2 AND org_type = ?3", nativeQuery = true)
  List<Long> findAuthAppIdsByTenantIdAndOrgIdAndOrgType(Long tenantId, Long orgId, String orgType);

  List<AuthPolicyOrg> findAllByPolicyIdAndGrantScope(Long policyId, PolicyGrantScope grantScope);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE app_id IN ?1", nativeQuery = true)
  void deleteByAppIdIn(Collection<Long> appIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE policy_id IN ?1", nativeQuery = true)
  void deleteByPolicyIdIn(Collection<Long> policyIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE tenant_id = ?1 AND app_id = ?2 AND default0 = ?3", nativeQuery = true)
  void deleteByTenantIdAndAppIdAndDefault(Long tenantId, Long appId, boolean default0);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE policy_id = ?1 AND org_type = ?2 AND org_id IN ?3", nativeQuery = true)
  void deleteByPolicyIdAndOrgTypeAndOrgIdIn(Long policyId, String orgType, Collection<Long> orgIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE org_id = ?1 AND org_type = ?2 AND policy_id IN ?3", nativeQuery = true)
  void deleteByOrgIdAndOrgTypeAndPolicyIdIn(Long orgId, String orgType, Collection<Long> policyIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE org_id IN ?1 AND org_type = ?2 AND policy_id IN ?3", nativeQuery = true)
  void deleteByOrgIdInAndOrgTypeAndPolicyIdIn(Collection<Long> orgIds, String orgType,
      Collection<Long> policyIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE org_id IN ?1 AND org_type = ?2", nativeQuery = true)
  void deleteByOrgIdInAndOrgType(Collection<Long> orgIds, String orgType);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE app_id = ?1 AND org_type = ?2 AND org_id IN ?3 AND policy_id IN ?4", nativeQuery = true)
  void deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(Long appId, String orgType,
      Collection<Long> orgIds, Collection<Long> policyIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_org WHERE app_id = ?1 AND org_type = ?2 AND org_id IN ?3", nativeQuery = true)
  void deleteByAppIdAndOrgTypeAndOrgIdIn(Long appId, String orgType, Collection<Long> orgIds);

}
