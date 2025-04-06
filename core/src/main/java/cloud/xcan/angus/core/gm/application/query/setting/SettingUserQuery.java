package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.UserApiProxy;

public interface SettingUserQuery {

  UserApiProxy findProxyByTenantId(Long optTenantId);

  SettingUser find(Long userId);

  SettingUser find0(Long userId);

}
