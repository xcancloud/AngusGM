package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;


public interface AuthPolicyFuncCmd {

  void add(Long policyId, Set<Long> appFuncIds);

  void replace(Long policyId, Set<Long> appFuncIds);

  void delete(Long policyId, Set<Long> appFuncIds);

  List<IdKey<Long, Object>> add0(List<AuthPolicy> policies, List<AppFunc> appFuncsDb);

  void replace0(List<AuthPolicy> policies, List<AppFunc> appFuncsDb);

}
