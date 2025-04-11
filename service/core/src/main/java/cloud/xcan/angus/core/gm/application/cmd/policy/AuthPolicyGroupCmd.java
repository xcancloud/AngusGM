package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface AuthPolicyGroupCmd {

  List<IdKey<Long, Object>> policyGroupAdd(Long policyId, List<AuthPolicyOrg> policyGroups);

  void policyGroupDelete(Long policyId, Set<Long> groupIds);

  List<IdKey<Long, Object>> groupPolicyAdd(Long groupId, List<AuthPolicyOrg> groupPolices);

  void groupPolicyDelete(Long groupId, Set<Long> policyIds);

  void groupPolicyDeleteBatch(HashSet<Long> groupIds, HashSet<Long> policyIds);

}
