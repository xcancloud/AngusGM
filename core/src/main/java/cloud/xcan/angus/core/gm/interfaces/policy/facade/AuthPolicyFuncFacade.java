package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
import java.util.List;
import java.util.Set;


public interface AuthPolicyFuncFacade {

  void add(Long policyId, Set<Long> appFuncIds);

  void replace(Long policyId, Set<Long> appFuncIds);

  void delete(Long policyId, Set<Long> appFuncIds);

  List<AuthPolicyFuncVo> list(Long policyId);

  List<AuthPolicyFuncTreeVo> tree(Long id);

}
