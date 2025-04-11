package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface AuthPolicyDeptCmd {

  List<IdKey<Long, Object>> policyDeptAdd(Long policyId, List<AuthPolicyOrg> policyDept);

  void policyDeptDelete(Long policyId, Set<Long> deptIds);

  List<IdKey<Long, Object>> deptPolicyAdd(Long deptId, List<AuthPolicyOrg> deptPolices);

  void deptPolicyDelete(Long deptId, Set<Long> policyIds);

  void deptPolicyDeleteBatch(HashSet<Long> deptIds, HashSet<Long> policyIds);
}
