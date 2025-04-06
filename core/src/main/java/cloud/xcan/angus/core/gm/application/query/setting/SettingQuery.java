package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;


public interface SettingQuery {

  Setting detail(SettingKey key);

  Setting find0(SettingKey pKey);

  Quota checkAndFindQuota(String name);
}
