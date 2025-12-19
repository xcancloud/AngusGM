package cloud.xcan.angus.core.gm.application.query.setting.impl;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.manager.SettingManager;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.SystemAssert;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Implementation of setting query operations.
 * </p>
 * <p>
 * Manages system setting retrieval, validation, and quota management. Provides comprehensive
 * setting querying with quota support.
 * </p>
 * <p>
 * Supports setting detail retrieval, quota validation, and setting management for comprehensive
 * system configuration administration.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class SettingQueryImpl implements SettingQuery {

  @Resource
  private SettingManager settingManager;

  /**
   * <p>
   * Retrieves detailed setting information by key.
   * </p>
   * <p>
   * Fetches complete setting record for the specified key. Returns setting with full configuration
   * details.
   * </p>
   */
  @Override
  public Setting detail(SettingKey key) {
    return new BizTemplate<Setting>() {

      @Override
      protected Setting process() {
        return settingManager.setting(key);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves setting by key without business template.
   * </p>
   * <p>
   * Returns setting directly from setting manager. Used for internal operations that don't require
   * business template.
   * </p>
   */
  @Override
  public Setting find0(SettingKey key) {
    return settingManager.setting(key);
  }

  /**
   * <p>
   * Validates and retrieves quota by name.
   * </p>
   * <p>
   * Validates quota resource existence and returns quota configuration. Throws ResourceNotFound if
   * quota resource or setting not found.
   * </p>
   */
  @Override
  public Quota checkAndFindQuota(String name) {
    QuotaResource quota;
    try {
      quota = QuotaResource.valueOf(name);
    } catch (Exception e) {
      throw ResourceNotFound.of(name, "QuotaResource");
    }

    List<Quota> quotas = find0(SettingKey.QUOTA).getQuota();
    SystemAssert.assertNotEmpty(quotas, "Quota initialization template not found");

    return quotas.stream().filter(x -> x.getName().equals(quota)).findFirst()
        .orElseThrow(() -> ResourceNotFound.of(name, "QuotaSetting"));
  }

}
