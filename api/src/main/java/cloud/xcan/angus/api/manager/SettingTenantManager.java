package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;

public interface SettingTenantManager {

  SettingTenant findSetting(Long tenantId);

  SettingTenant findSetting(String invitationCode);

  SettingTenant checkAndFindSetting(Long tenantId);

  SettingTenant checkAndFindSetting(String invitationCode);

  String getCachedSetting(Long tenantId);

  SettingTenant parseCachedSetting(String jsonString);
}
