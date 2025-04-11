package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface AuthPolicyUserCmd {

  List<IdKey<Long, Object>> policyUserAdd(Long policyId, List<AuthPolicyOrg> policyUsers);

  void policyUserDelete(Long policyId, Set<Long> userIds);

  List<IdKey<Long, Object>> userPolicyAdd(Long userId, List<AuthPolicyOrg> userPolicies);

  void userPolicyDelete(Long userId, Set<Long> policyIds);

  void userPolicyDeleteBatch(HashSet<Long> userIds, HashSet<Long> policyIds);
}
