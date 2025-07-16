package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler;


import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppFuncVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppTreeVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppVo;
import java.util.List;
import java.util.Objects;

public class AuthUserAssembler {

  public static AuthAppFuncVo toAuthFuncVo(AppFunc appFunc) {
    return Objects.isNull(appFunc) ? null : new AuthAppFuncVo()
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
        .setHasAuth(appFunc.getHasAuth())
        .setApis(AppFuncAssembler.toAppApiVos(appFunc.getApis()))
        .setTags(AppFuncAssembler.toTagVos(appFunc.getTags()));
  }

  public static AuthAppFuncTreeVo toAuthAppFuncTreeVo(AuthAppFuncVo appFuncVo) {
    return Objects.isNull(appFuncVo) ? null : new AuthAppFuncTreeVo()
        .setId(appFuncVo.getId())
        .setCode(appFuncVo.getCode())
        .setName(appFuncVo.getName())
        .setShowName(appFuncVo.getShowName())
        .setPid(appFuncVo.getPid())
        .setEnabled(appFuncVo.getEnabled())
        .setIcon(appFuncVo.getIcon())
        .setType(appFuncVo.getType())
        .setAuthCtrl(appFuncVo.getAuthCtrl())
        .setUrl(appFuncVo.getUrl())
        .setSequence(appFuncVo.getSequence())
        .setHasAuth(appFuncVo.getHasAuth())
        .setApis(appFuncVo.getApis())
        .setTags(appFuncVo.getTags());
  }

  public static AuthAppVo toAuthAppVo(App app, List<AuthAppFuncVo> funcVos) {
    return new AuthAppVo().setId(app.getId())
        .setCode(app.getCode())
        .setVersion(app.getVersion())
        .setName(app.getName())
        .setShowName(app.getShowName())
        .setIcon(app.getIcon())
        .setType(app.getType())
        .setAuthCtrl(app.getAuthCtrl())
        .setUrl(app.getUrl())
        .setSequence(app.getSequence())
        .setAppFuncs(funcVos)
        .setApis(AppFuncAssembler.toApiVos(app.getApis()));
  }

  public static AuthAppTreeVo toAuthAppTreeVo(App app, List<AuthAppFuncTreeVo> funcVosTree) {
    return new AuthAppTreeVo().setId(app.getId())
        .setCode(app.getCode())
        .setVersion(app.getVersion())
        .setName(app.getName())
        .setShowName(app.getShowName())
        .setIcon(app.getIcon())
        .setType(app.getType())
        .setAuthCtrl(app.getAuthCtrl())
        .setUrl(app.getUrl())
        .setSequence(app.getSequence())
        .setAppFuncs(funcVosTree);
  }
}
