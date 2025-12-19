package cloud.xcan.angus.core.gm.application.query.setting.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant setting query operations.
 * </p>
 * <p>
 * Manages tenant setting retrieval, validation, and timezone configuration. Provides comprehensive
 * tenant setting querying with locale support.
 * </p>
 * <p>
 * Supports tenant setting detail retrieval, timezone management, and setting validation for
 * comprehensive tenant configuration administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class SettingTenantQueryImpl implements SettingTenantQuery {

  @Resource
  private SettingTenantRepo settingTenantRepo;

  /**
   * <p>
   * Retrieves detailed tenant setting information by tenant ID.
   * </p>
   * <p>
   * Fetches complete tenant setting record with timezone configuration. Loads default timezone from
   * application configuration.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves tenant setting by tenant ID with validation.
   * </p>
   * <p>
   * Returns tenant setting with existence validation. Throws ResourceNotFound if tenant setting
   * does not exist.
   * </p>
   */
  @Override
  public SettingTenant find(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElseThrow(() ->
        ResourceNotFound.of(tenantId, "TenantSetting"));
  }

  /**
   * <p>
   * Retrieves tenant setting by tenant ID without validation.
   * </p>
   * <p>
   * Returns tenant setting without existence validation. Returns null if tenant setting does not
   * exist.
   * </p>
   */
  @Override
  public SettingTenant find0(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElse(null);
  }
}
