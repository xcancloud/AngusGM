package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.api.gm.app.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.app.AppPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.app.AppPolicyVo;
import cloud.xcan.angus.remote.PageResult;

public interface AuthPolicyAppFacade {

  PageResult<AppPolicyVo> appPolicyList(Long appId, AppPolicyFindDto dto);

  AppDetailVo policyAppDetail(Long policyId);
}
