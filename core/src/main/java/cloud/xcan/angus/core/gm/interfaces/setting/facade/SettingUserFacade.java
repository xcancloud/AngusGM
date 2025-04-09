package cloud.xcan.angus.core.gm.interfaces.setting.facade;

import cloud.xcan.angus.api.enums.SocialType;
import cloud.xcan.angus.api.gm.setting.dto.UserSocialUpdateDto;
import cloud.xcan.angus.api.gm.setting.vo.UserApiProxyVo;
import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserApiClientProxyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserApiProxyEnabledDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserPreferenceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.user.UserSocialBindingVo;

public interface SettingUserFacade {

  void preferenceUpdate(UserPreferenceUpdateDto dto);

  UserPreferenceVo preferenceDetail();

  void proxyUpdate(UserApiClientProxyUpdateDto dto);

  void proxyEnabled(UserApiProxyEnabledDto dto);

  UserApiProxyVo proxyDetail();

  UserApiProxyVo proxyDetailDoor(Long tenantId);

  void socialBindingUpdate(UserSocialUpdateDto dto);

  void socialUnbind(SocialType type);

  UserSocialBindingVo socialBindDetail();


}
