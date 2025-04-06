package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;


public interface AuthPolicyCmd {

  List<IdKey<Long, Object>> add(List<AuthPolicy> policies);

  void update(List<AuthPolicy> policies);

  List<IdKey<Long, Object>> replace(List<AuthPolicy> policies);

  void delete(Set<Long> ids);

  void enabled(List<AuthPolicy> policies);

  void initAndOpenAppByPolicy(Long policyId);
}
