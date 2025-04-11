package cloud.xcan.angus.core.gm.application.cmd.setting;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;


public interface SettingCmd {

  void update(SettingKey key, Setting setting);

}
