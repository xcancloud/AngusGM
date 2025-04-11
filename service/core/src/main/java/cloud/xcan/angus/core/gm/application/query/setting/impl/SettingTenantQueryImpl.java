package cloud.xcan.angus.core.gm.application.query.setting.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class SettingTenantQueryImpl implements SettingTenantQuery {

  @Resource
  private SettingTenantRepo settingTenantRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SettingTenant detail(Long tenantId) {
    return new BizTemplate<SettingTenant>() {

      @Override
      protected SettingTenant process() {
        SettingTenant tenantSetting = find0(tenantId);
        if (Objects.nonNull(tenantSetting)) {
          // Load TimeZone in configuration
          tenantSetting.getLocaleData().setDefaultTimeZone(getApplicationInfo().getTimezone());
          return tenantSetting;
        }
        return tenantSetting;
      }
    }.execute();
  }

  @Override
  public SettingTenant find(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElseThrow(() ->
        ResourceNotFound.of(tenantId, "TenantSetting"));
  }

  @Override
  public SettingTenant find0(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElse(null);
  }
}
