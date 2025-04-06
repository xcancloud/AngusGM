package cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler.AuthUserAssembler.toAuthAppTreeVo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyUserQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.AuthUserFacade;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.AuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.AuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.AuthUserRealNameUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.CurrentAuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.CurrentAuthUserPasswordInitDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.CurrentAuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler.AuthUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppFuncTreeVo;
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

  @Override
  public List<AppVo> userAppList(Long userId) {
    List<App> apps = authPolicyUserQuery.userAppList(userId);
    return apps.stream().map(AppAssembler::toVo).collect(Collectors.toList());
  }

  @Override
  public AuthAppVo userAppFuncList(Long userId, String appIdOrCode, Boolean joinApi,
      Boolean joinTag, Boolean onlyEnabled) {
    App app = authPolicyUserQuery.userAppFuncList(userId, appIdOrCode,
        nullSafe(joinApi, false), nullSafe(joinTag, false), nullSafe(onlyEnabled, true));
    List<AuthAppFuncVo> funcVos = app.getAppFunc().stream().map(AuthUserAssembler::toAuthFuncVo)
        .collect(Collectors.toList());
    return AuthUserAssembler.toAuthAppVo(app, funcVos);
  }

  @Override
  public AuthAppTreeVo appFuncTree(Long userId, String appIdOrCode, Boolean joinApi,
      Boolean joinTag, Boolean onlyEnabled) {
    App app = authPolicyUserQuery.userAppFuncList(userId, appIdOrCode,
        nullSafe(joinApi, false), nullSafe(joinTag, false), nullSafe(onlyEnabled, true));
    List<AuthAppFuncVo> funcVos = app.getAppFunc().stream().map(AuthUserAssembler::toAuthFuncVo)
        .collect(Collectors.toList());
    List<AuthAppFuncTreeVo> funcVosTree = isEmpty(funcVos) ? null :
        TreeUtils.toTree(funcVos.stream().map(AuthUserAssembler::toAuthAppFuncTreeVo)
            .collect(Collectors.toList()), true);
    return toAuthAppTreeVo(app, funcVosTree);
  }
}
