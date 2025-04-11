package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
import java.util.Objects;


public class AuthPolicyFuncAssembler {

  public static AuthPolicyFuncVo toPolicyFuncVo(AppFunc appFunc) {
    return Objects.isNull(appFunc) ? null : new AuthPolicyFuncVo()
        .setId(appFunc.getId())
        .setCode(appFunc.getCode())
        .setName(appFunc.getName())
        .setShowName(appFunc.getShowName())
        .setPid(appFunc.getPid())
        .setEnabled(appFunc.getEnabled())
        .setIcon(appFunc.getIcon())
        .setType(appFunc.getType())
        .setAuthCtrl(appFunc.getAuthCtrl())
        .setUrl(appFunc.getUrl())
        .setSequence(appFunc.getSequence())
        .setApis(AppFuncAssembler.toApiVos(appFunc.getApis()));
  }

}
