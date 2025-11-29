package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.setting.SettingKey.LOCALE;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.SECURITY;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;

public class SettingTenantConverter {

  public static SettingTenant initTenantSetting(Long tenantId, SettingQuery settingQuery) {
    SettingTenant tenant = new SettingTenant();
    tenant.setId(tenantId);
    tenant.setTenantId(tenantId);
    tenant.setLocaleData(settingQuery.find0(LOCALE).getLocale());
    tenant.setSecurityData(settingQuery.find0(SECURITY).getSecurity());
    return tenant;
  }

}
