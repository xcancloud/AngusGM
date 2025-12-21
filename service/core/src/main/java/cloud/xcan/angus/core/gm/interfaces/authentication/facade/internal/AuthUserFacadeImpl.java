package cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthUserAssembler.toAuthAppTreeVo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.gm.app.vo.AuthAppFuncTreeVo;
import cloud.xcan.angus.core.biz.MessageJoin;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthUserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyUserQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthUserFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthUserRealNameUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.CurrentAuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.CurrentAuthUserPasswordInitDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.CurrentAuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppFuncVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppTreeVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppVo;
import cloud.xcan.angus.spec.utils.TreeUtils;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class AuthUserFacadeImpl implements AuthUserFacade {

  @Resource
  private AuthUserCmd userCmd;

  @Resource
  private AuthUserQuery authUserQuery;

  @Resource
  private AuthUserCurrentCmd authUserCurrentCmd;

  @Resource
  private AuthPolicyUserQuery authPolicyUserQuery;

  @Resource
  private AuthUserFacade authUserFacade;

  @Override
  public void updateCurrentPassword(AuthUserPasswordUpdateDto dto) {
    userCmd.passwordUpdate(dto.getId(), dto.getNewPassword());
  }

  @Override
  public void checkPassword(AuthUserPasswordCheckDto dto) {
    authUserQuery.checkPassword(dto.getId(), dto.getPassword());
  }

  @Override
  public void delete(HashSet<Long> ids) {
    userCmd.delete(ids);
  }

  @Override
  public void realname(AuthUserRealNameUpdateDto dto) {
    userCmd.realName(dto.getTenantId(), dto.getRealNameStatus());
  }

  @Override
  public void updateCurrentPassword(CurrentAuthUserPasswordUpdateDto dto) {
    authUserCurrentCmd.updateCurrentPassword(dto.getOldPassword(), dto.getNewPassword());
  }

  @Override
  public void checkCurrentPassword(CurrentAuthUserPasswordCheckDto dto) {
    authUserCurrentCmd.checkCurrentPassword(dto.getPassword());
  }

  @Override
  public void initCurrentPassword(CurrentAuthUserPasswordInitDto dto) {
    authUserCurrentCmd.initCurrentPassword(dto.getNewPassword());
  }

  @MessageJoin
  @Override
  public List<AppVo> userAppList(Long userId) {
    List<App> apps = authPolicyUserQuery.userAppList(userId);
    return apps.stream().map(AppAssembler::toVo).collect(Collectors.toList());
  }

  @MessageJoin
  @Override
  public AuthAppVo userAppFuncList(Long userId, String appIdOrCode, Boolean joinApi,
      Boolean onlyEnabled) {
    App app = getApp(userId, appIdOrCode, joinApi, onlyEnabled);
    List<AuthAppFuncVo> funcVos = authUserFacade.getAuthAppFuncVos(app);
    return AuthUserAssembler.toAuthAppVo(app, funcVos);
  }

  @MessageJoin
  @Override
  public AuthAppTreeVo appFuncTree(Long userId, String appIdOrCode, Boolean joinApi,
      Boolean onlyEnabled) {
    App app = getApp(userId, appIdOrCode, joinApi, onlyEnabled);
    List<AuthAppFuncVo> funcVos = authUserFacade.getAuthAppFuncVos(app);
    List<AuthAppFuncTreeVo> funcVosTree = isEmpty(funcVos) ? null :
        TreeUtils.toTree(funcVos.stream().map(AuthUserAssembler::toAuthAppFuncTreeVo)
            .collect(Collectors.toList()), true);
    return toAuthAppTreeVo(app, funcVosTree);
  }

  @Override
  public App getApp(Long userId, String appIdOrCode, Boolean joinApi, Boolean onlyEnabled) {
    return authPolicyUserQuery.userAppFuncList(userId, appIdOrCode,
        nullSafe(joinApi, false), nullSafe(onlyEnabled, true));
  }

  @MessageJoin
  @Override
  public List<AuthAppFuncVo> getAuthAppFuncVos(App app) {
    return app.getAppFunc().stream().map(AuthUserAssembler::toAuthFuncVo)
        .collect(Collectors.toList());
  }
}
