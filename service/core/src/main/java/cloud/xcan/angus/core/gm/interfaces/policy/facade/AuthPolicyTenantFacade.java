package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org.AuthAppDefaultPolicyVo;
import java.util.List;


public interface AuthPolicyTenantFacade {

  void defaultPolicySet(Long appId, Long policyId);

  void defaultPolicyDelete(Long appId);

  List<AuthAppDefaultPolicyVo> defaultPolicy();

  List<AppVo> tenantAppList();

  List<AuthPolicyFuncVo> tenantAppFuncList(Long appId);

  List<AuthPolicyFuncTreeVo> tenantAppFuncTree(Long appId);
}
