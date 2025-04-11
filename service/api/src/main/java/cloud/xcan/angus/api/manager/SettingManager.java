package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;

public interface SettingManager {

  Setting setting(SettingKey key);

  Setting getCachedSetting(SettingKey key);
}
