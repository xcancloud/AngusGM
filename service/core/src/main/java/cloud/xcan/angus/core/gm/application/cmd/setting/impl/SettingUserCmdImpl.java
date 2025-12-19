package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.core.gm.application.converter.SettingUserConverter.initUserSetting;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUserRepo;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.commonlink.setting.user.socialbinding.SocialBinding;
import cloud.xcan.angus.api.enums.SocialType;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantQuotaCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingUserCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingUserQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of user setting command operations.
 * </p>
 * <p>
 * Manages user-specific settings including preferences, API proxy configurations, and social
 * binding information.
 * </p>
 * <p>
 * Provides user setting initialization, updates, and synchronization with tenant-level
 * configurations.
 * </p>
 */
@org.springframework.stereotype.Service
public class SettingUserCmdImpl extends CommCmd<SettingUser, Long> implements SettingUserCmd {

  @Resource
  private SettingUserRepo settingUserRepo;
  @Resource
  private SettingUserQuery settingUserQuery;
  @Resource
  private SettingTenantQuery settingTenantQuery;
  @Resource
  private SettingTenantCmd settingTenantCmd;
  @Resource
  private SettingTenantQuotaCmd settingTenantQuotaCmd;

  /**
   * <p>
   * Finds and initializes user settings.
   * </p>
   * <p>
   * Retrieves existing user settings or creates new ones with default values. Loads timezone
   * configuration and refreshes API proxy settings from tenant configuration.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public SettingUser findAndInit(Long userId) {
    return new BizTemplate<SettingUser>() {
      @Override
      protected SettingUser process() {
        SettingUser userSetting = settingUserQuery.find0(userId);
        SettingTenant tenantSetting = settingTenantQuery.detail(getOptTenantId());
        if (nonNull(userSetting)) {
          // Load timezone from application configuration
          userSetting.getPreference().setDefaultTimeZone(getApplicationInfo().getTimezone());
          return userSetting;
        }
        // Initialize new user settings
        userSetting = initUserSetting(getOptTenantId(), tenantSetting);
        userSetting.setId(userId);
        // Load timezone from application configuration
        userSetting.getPreference().setDefaultTimeZone(getApplicationInfo().getTimezone());
        settingUserRepo.save(userSetting);
        return userSetting;
      }
    }.execute();
  }

  /**
   * <p>
   * Initializes tenant and user settings for new users.
   * </p>
   * <p>
   * Creates tenant settings and user settings when they don't exist, ensuring proper initialization
   * for new tenant users.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void tenantAndUserInit(Long tenantId, Boolean initTenant, Long userId) {
    new BizTemplate<Void>(false) {
      @Override
      protected Void process() {
        if (!settingUserRepo.existsByTenantId(userId)) {
          if (initTenant) {
            // Initialize tenant settings
            settingTenantCmd.init(tenantId);
            settingTenantQuotaCmd.init(tenantId);
          }
          // Initialize user settings
          SettingTenant tenantSetting = settingTenantQuery.find(tenantId);
          insert0(initUserSetting(tenantId, tenantSetting));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates user preference settings.
   * </p>
   * <p>
   * Updates user preferences with new values while preserving existing settings that are not
   * provided in the update.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void preferenceUpdate(Preference preference) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SettingUser settingDb = settingUserQuery.find(getUserId());
        CoreUtils.copyPropertiesIgnoreNull(preference, settingDb.getPreference());
        settingUserRepo.save(settingDb);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates social binding information for user.
   * </p>
   * <p>
   * Updates social account binding with current timestamp and saves the binding information to user
   * settings.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void socialBindingUpdate(SocialType type, SocialBinding socialBinding) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SettingUser settingDb = settingUserQuery.find(getUserId());
        switch (type) {
          case GITHUB:
            socialBinding.setGithubUserBindDate(LocalDateTime.now());
            break;
          case WECHAT:
            socialBinding.setWechatUserBindDate(LocalDateTime.now());
            break;
          case GOOGLE:
            socialBinding.setGoogleUserBindDate(LocalDateTime.now());
            break;
          default:
            // NOOP for unsupported social types
        }
        settingDb.setSocialBind(socialBinding);
        settingUserRepo.save(settingDb);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes social binding for user.
   * </p>
   * <p>
   * Unbinds the specified social account type by clearing the user ID and binding date for that
   * social platform.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void socialUnbind(SocialType type) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SettingUser settingDb = settingUserQuery.find(getUserId());
        SocialBinding socialBinding = settingDb.getSocialBind();
        if (isNull(socialBinding)) {
          return null;
        }
        switch (type) {
          case GITHUB:
            socialBinding.setGithubUserId(null).setGithubUserBindDate(null);
            break;
          case WECHAT:
            socialBinding.setWechatUserId(null).setWechatUserBindDate(null);
            break;
          case GOOGLE:
            socialBinding.setGoogleUserId(null).setGoogleUserBindDate(null);
            break;
          default:
            // NOOP for unsupported social types
        }
        settingDb.setSocialBind(socialBinding);
        settingUserRepo.save(settingDb);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SettingUser, Long> getRepository() {
    return settingUserRepo;
  }
}
