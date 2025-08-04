package cloud.xcan.angus.core.gm.interfaces.auth.facade;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.AuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.AuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.AuthUserRealNameUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.CurrentAuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.CurrentAuthUserPasswordInitDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.CurrentAuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppFuncVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppTreeVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppVo;
import java.util.HashSet;
import java.util.List;

public interface AuthUserFacade {

  void updateCurrentPassword(AuthUserPasswordUpdateDto dto);

  void checkPassword(AuthUserPasswordCheckDto dto);

  void delete(HashSet<Long> ids);

  void realname(AuthUserRealNameUpdateDto dtos);

  void updateCurrentPassword(CurrentAuthUserPasswordUpdateDto dto);

  void checkCurrentPassword(CurrentAuthUserPasswordCheckDto dto);

  void initCurrentPassword(CurrentAuthUserPasswordInitDto dto);

  List<AppVo> userAppList(Long userId);

  AuthAppVo userAppFuncList(Long userId, String appIdOrCode, Boolean joinApi, Boolean onlyEnabled);

  AuthAppTreeVo appFuncTree(Long userId, String appIdOrCode, Boolean joinApi, Boolean onlyEnabled);

  App getApp(Long userId, String appIdOrCode, Boolean joinApi, Boolean onlyEnabled);

  List<AuthAppFuncVo> getAuthAppFuncVos(App app);
}
