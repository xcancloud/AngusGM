package cloud.xcan.angus.core.gm.application.cmd.setting;

import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.ApiProxyType;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.commonlink.setting.user.socialbinding.SocialBinding;
import cloud.xcan.angus.api.enums.SocialType;

public interface SettingUserCmd {

  SettingUser findAndInit(Long userId);

  void tenantAndUserInit(Long tenantId, Boolean initTenant, Long userId);

  void preferenceUpdate(Preference preference);

  void proxyUpdate(String url);

  void proxyEnabled(ApiProxyType name);

  void socialBindingUpdate(SocialType type, SocialBinding socialBinding);

  void socialUnbind(SocialType type);


}
