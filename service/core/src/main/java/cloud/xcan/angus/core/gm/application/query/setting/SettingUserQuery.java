package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;

public interface SettingUserQuery {

  SettingUser find(Long userId);

  SettingUser find0(Long userId);

}
