package cloud.xcan.angus.core.gm.application.cmd.app;

import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface AppOrgAuthCmd {

  List<IdKey<Long, Object>> authUserPolicy(Long appId, HashSet<Long> userIds,
      HashSet<Long> policyIds);

  List<IdKey<Long, Object>> authDeptPolicy(Long appId, HashSet<Long> deptIds,
      HashSet<Long> policyIds);

  List<IdKey<Long, Object>> authGroupPolicy(Long appId, HashSet<Long> groupIds,
      HashSet<Long> policyIds);

  void authUserPolicyDelete(Long appId, HashSet<Long> userIds, HashSet<Long> policyIds);

  void authDeptPolicyDelete(Long appId, HashSet<Long> deptIds, HashSet<Long> policyIds);

  void authGroupPolicyDelete(Long appId, HashSet<Long> groupIds, HashSet<Long> policyIds);

}
