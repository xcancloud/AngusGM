package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFunc;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface AuthPolicyFuncQuery {

  List<AppFunc> list(Long policyId);

  List<AuthPolicyFunc> findByPolicyId(Long policyId);

  List<AuthPolicyFunc> findByPolicyId(Collection<Long> policyIds);

  List<AppFunc> findValidFuncByPolicyId(Collection<Long> policyIds);

  Set<Long> findExistedFuncIdsByPolicyId(Long policyId);

}
