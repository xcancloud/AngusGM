package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;

public class SettingUserConverter {

  public static SettingUser initUserSetting(Long tenantId, SettingTenant tenantSetting) {
    SettingUser user = new SettingUser();
    user.setTenantId(tenantId);
    Preference preferenceData = new Preference()
        .setLanguage(tenantSetting.getLocaleData().getDefaultLanguage())
        .setDefaultTimeZone(getApplicationInfo().getTimezone());
    user.setPreference(preferenceData);
    return user;
  }

}
