package cloud.xcan.angus.api.commonlink.authuser;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonAuthUserRepo")
public interface AuthUserRepo extends BaseRepository<AuthUser, Long> {

  List<AuthUser> findAllByIdIn(Collection<String> ids);

  List<AuthUser> findAllByTenantId(String tenantId);

  @Query(value = "SELECT * FROM oauth2_user WHERE username = ?1 OR mobile = ?1 OR email = ?1 AND deleted = false", nativeQuery = true)
  List<AuthUser> findByAccount(String account);

  List<AuthUser> findByEmail(String email);

  List<AuthUser> findByMobile(String mobile);

  AuthUser findByUsername(String username);

  @Query(value =
      "SELECT a.api_operation_id FROM auth_api_authority a "
          + "  WHERE a.service_code = ?1 AND a.api_id IN ( "
          + "   SELECT pf.id"
          + "     FROM auth_policy_func pf, app_func af"
          + "     WHERE pf.func_id = af.id AND af.enabled = 1"
          + "       AND pf.policy_id IN ("
          + "          SELECT p.id FROM auth_policy p WHERE p.id IN ("
          + "              SELECT pu.policy_id FROM policy_user pu WHERE pu.user_id = ?2"
          + "              UNION"
          + "              SELECT pg.policy_id FROM policy_group pg WHERE pg.group_id IN ?3"
          + "              UNION"
          + "              SELECT pd.policy_id FROM policy_dept pd WHERE pd.dept_id IN ?4"
          + "              UNION"
          + "              SELECT pt.policy_id FROM auth_policy_tenant pt WHERE pt.tenant_id = ?5"
          + "              ) AND p.enabled = 1"
          + "        )"
          + "      )"
          + "  AND a.api_enabled = 1 AND a.service_enabled = 1", nativeQuery = true)
  List<String> findUserGrantServiceApis(String serviceCode, Long userId, Collection<Long> groupIds,
      Collection<Long> deptIds, Long tenantId);

  @Query(value =
      "SELECT a.api_operation_id FROM auth_api_authority a "
          + "  WHERE a.api_id IN ( "
          + "   SELECT pf.id"
          + "     FROM auth_policy_func pf, app_func af"
          + "     WHERE pf.func_id = af.id AND af.enabled = 1"
          + "       AND pf.policy_id IN ("
          + "          SELECT p.id FROM auth_policy p WHERE p.id IN ("
          + "              SELECT pu.policy_id FROM policy_user pu WHERE pu.user_id = ?1"
          + "              UNION"
          + "              SELECT pg.policy_id FROM policy_group pg WHERE pg.group_id IN ?2"
          + "              UNION"
          + "              SELECT pd.policy_id FROM policy_dept pd WHERE pd.dept_id IN ?3"
          + "              UNION"
          + "              SELECT pt.policy_id FROM auth_policy_tenant pt WHERE pt.tenant_id = ?4"
          + "              ) AND p.enabled = 1"
          + "        )"
          + "      )"
          + "  AND a.api_enabled = 1 AND a.service_enabled = 1", nativeQuery = true)
  List<String> findUserGrantApis(Long userId, Collection<Long> groupIds,
      Collection<Long> deptIds, Long tenantId);

  @Modifying
  @Query(value = "UPDATE oauth2_user SET tenant_real_name_status = ?2 WHERE tenant_id = ?1", nativeQuery = true)
  void updateStatusByTenantId(String tenantId, String realNameStatus);

  @Modifying
  @Query(value = "UPDATE oauth2_user SET to_user = ?2 WHERE id IN ?1", nativeQuery = true)
  void updateToUserByIdIn(Collection<String> userIds, Boolean toUser);

  @Modifying
  @Query(value = "DELETE FROM oauth2_user WHERE id in ?1", nativeQuery = true)
  void deleteByIdIn(Collection<String> ids);

}
