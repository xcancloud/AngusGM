package cloud.xcan.angus.core.gm.application.query.setting.impl;

import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUserRepo;

import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingUserQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * Implementation of user setting query operations.
 * </p>
 * <p>
 * Manages user setting retrieval, validation, and proxy configuration. Provides comprehensive user
 * setting querying with tenant setting support.
 * </p>
 * <p>
 * Supports user setting detail retrieval, proxy configuration assembly, and tenant setting
 * integration for comprehensive user configuration administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class SettingUserQueryImpl implements SettingUserQuery {

  @Resource
  private SettingUserRepo settingUserRepo;
  @Resource
  private SettingTenantQuery settingTenantQuery;

  /**
   * <p>
   * Retrieves user setting by user ID with validation.
   * </p>
   * <p>
   * Returns user setting with existence validation. Throws ResourceNotFound if user setting does
   * not exist.
   * </p>
   */
  @Override
  public SettingUser find(Long userId) {
    return settingUserRepo.findById(userId).orElseThrow(
        () -> ResourceNotFound.of(userId, "UserSetting"));
  }

  /**
   * <p>
   * Retrieves user setting by user ID without validation.
   * </p>
   * <p>
   * Returns user setting without existence validation. Returns null if user setting does not
   * exist.
   * </p>
   */
  @Override
  public SettingUser find0(Long userId) {
    SettingUser settingUser = settingUserRepo.findById(userId).orElse(null);
    if (Objects.isNull(settingUser)) {
      return null;
    }
    return settingUser;
  }
}
