package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.setting.SettingKey.FUNC_INDICATOR;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.LOCALE;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.PREF_INDICATOR;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.SECURITY;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.STABILITY_INDICATOR;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.TESTER_EVENT;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;

public class SettingTenantConverter {

  public static SettingTenant initTenantSetting(Long tenantId, SettingQuery settingQuery) {
    SettingTenant tenant = new SettingTenant();
    tenant.setId(tenantId);
    tenant.setTenantId(tenantId);
    tenant.setLocaleData(settingQuery.find0(LOCALE).getLocale());
    tenant.setFuncData(settingQuery.find0(FUNC_INDICATOR).getFunc());
    tenant.setPerfData(settingQuery.find0(PREF_INDICATOR).getPerf());
    tenant.setStabilityData(settingQuery.find0(STABILITY_INDICATOR).getStability());
    tenant.setSecurityData(settingQuery.find0(SECURITY).getSecurity());
    tenant.setTesterEventData(settingQuery.find0(TESTER_EVENT).getTesterEvent());
    return tenant;
  }

}
