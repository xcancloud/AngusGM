package cloud.xcan.angus.core.gm.domain.app.func;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface AppFuncRepo extends BaseRepository<AppFunc, Long>,
    NameJoinRepository<AppFunc, Long> {

  List<AppFunc> findByAppIdAndIdIn(Long appId, Collection<Long> ids);

  List<AppFunc> findByAppIdAndPidIn(Long appId, Collection<Long> pids);

  List<AppFunc> findAllByAppId(Long appId);

  List<AppFunc> findAllByIdIn(Collection<Long> ids);

  List<AppFunc> findAllByAppIdAndEnabled(Long appId, Boolean enabled);

  List<AppFunc> findAllByAppIdAndType(Long appIdi, AppFuncType funcType);

  List<AppFunc> findAllByAppIdIn(Collection<Long> appIds);

  List<AppFunc> findByAppIdAndCodeIn(Long appId, Collection<String> codes);

  List<AppFunc> findByIdInAndEnabledOrderBySequenceAsc(Collection<Long> authFuncIds,
      boolean enabled);

  @Query(value = "SELECT * FROM app_func WHERE id IN (SELECT DISTINCT func_id FROM auth_policy_func WHERE policy_id IN ?1) AND enabled = 1", nativeQuery = true)
  List<AppFunc> findValidFuncByPolicyId(Collection<Long> policyIds);

  @Query(value = "SELECT * FROM app_func WHERE id IN (SELECT DISTINCT func_id FROM auth_policy_func WHERE policy_id IN ?1)", nativeQuery = true)
  List<AppFunc> findFuncByPolicyId(Collection<Long> policyIds);

  @Modifying
  @Query("DELETE FROM AppFunc af WHERE af.appId = ?1 AND af.id IN ?2")
  void deleteByAppIdAndIdIn(Long id, Collection<Long> ids);

  @Modifying
  @Query("DELETE FROM AppFunc af WHERE af.appId IN ?1")
  void deleteByAppIdIn(Collection<Long> ids);
}
