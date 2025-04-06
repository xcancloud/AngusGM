package cloud.xcan.angus.core.gm.domain.policy.func;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AuthPolicyFuncRepo extends BaseRepository<AuthPolicyFunc, Long> {

  List<AuthPolicyFunc> findByPolicyId(Long policyId);

  List<AuthPolicyFunc> findByPolicyIdIn(Collection<Long> policyIds);

  List<AuthPolicyFunc> findAllByAppIdIn(Collection<Long> appIds);

  List<AuthPolicyFunc> findAllByAppIdAndPolicyId(Long appId, Long policyId);

  @Query(value = "SELECT func_id FROM auth_policy_func WHERE policy_id IN ?1", nativeQuery = true)
  Set<Long> findFuncIdsByPolicyIdIn(Collection<Long> policyIds);

  @Query(value = "SELECT func_id FROM auth_policy_func WHERE policy_id = ?1", nativeQuery = true)
  Set<Long> findFuncIdsByPolicyId(Long policyId);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_func WHERE policy_id IN ?1", nativeQuery = true)
  void deleteByPolicyIdIn(Collection<Long> policyIds);

  @Modifying
  @Query(value = "DELETE FROM auth_policy_func WHERE policy_id = ?1 AND func_id IN ?2", nativeQuery = true)
  void deleteByPolicyIdAndFuncIdIn(Long policyId, Collection<Long> appFuncIds);

}
