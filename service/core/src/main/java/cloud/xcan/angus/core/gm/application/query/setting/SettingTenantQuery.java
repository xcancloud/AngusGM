package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;


public interface SettingTenantQuery {

  SettingTenant detail(Long tenantId);

  SettingTenant find(Long tenantId);

  SettingTenant find0(Long tenantId);

}
