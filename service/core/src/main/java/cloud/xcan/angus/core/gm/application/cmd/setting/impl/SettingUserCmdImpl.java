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
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.ApiProxyType;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.UserApiProxy;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.commonlink.setting.user.socialbinding.SocialBinding;
import cloud.xcan.angus.api.enums.SocialType;
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public SettingUser findAndInit(Long userId) {
    return new BizTemplate<SettingUser>() {

      @Override
      protected SettingUser process() {
        SettingUser userSetting = settingUserQuery.find0(userId);
        SettingTenant tenantSetting = settingTenantQuery.detail(getOptTenantId());
        if (nonNull(userSetting)) {
          // Load TimeZone in configuration
          userSetting.getPreference().setDefaultTimeZone(getApplicationInfo().getTimezone());
          // Load latest server proxy url without persistence
          UserApiProxy apiProxy = userSetting.getApiProxy().copy();
          apiProxy.getServerProxy().setUrl(nonNull(tenantSetting.getServerApiProxyData())
              ? tenantSetting.getServerApiProxyData().getUrl() : null);
          userSetting.setApiProxyRefresh(apiProxy);
          return userSetting;
        }
        userSetting = initUserSetting(getOptTenantId(), tenantSetting);
        userSetting.setId(userId);
        // Load TimeZone in configuration
        userSetting.getPreference().setDefaultTimeZone(getApplicationInfo().getTimezone());
        settingUserRepo.save(userSetting);
        return userSetting;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void tenantAndUserInit(Long tenantId, Boolean initTenant, Long userId) {
    new BizTemplate<Void>(false) {

      @Override
      protected Void process() {
        if (!settingUserRepo.existsByTenantId(userId)) {
          if (initTenant) {
            settingTenantCmd.init(tenantId);
            settingTenantQuotaCmd.init(tenantId);
          }
          SettingTenant tenantSetting = settingTenantQuery.find(tenantId);
          insert0(initUserSetting(tenantId, tenantSetting));
        }
        return null;
      }
    }.execute();
  }

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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void proxyUpdate(String url) {
    new BizTemplate<Void>() {


      @Override
      protected Void process() {
        SettingUser settingDb = settingUserQuery.find(getUserId());
        settingDb.getApiProxy().getClientProxy().setUrl(url);
        settingUserRepo.save(settingDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void proxyEnabled(ApiProxyType proxyType) {
    new BizTemplate<Void>() {


      @Override
      protected Void process() {
        SettingUser settingDb = settingUserQuery.find(getUserId());
        switch (proxyType) {
          case NO_PROXY:
            settingDb.getApiProxy().getNoProxy().setEnabled(true);
            settingDb.getApiProxy().getClientProxy().setEnabled(false);
            settingDb.getApiProxy().getServerProxy().setEnabled(false);
            settingDb.getApiProxy().getCloudProxy().setEnabled(false);
            break;
          case CLIENT_PROXY:
            settingDb.getApiProxy().getNoProxy().setEnabled(false);
            settingDb.getApiProxy().getClientProxy().setEnabled(true);
            settingDb.getApiProxy().getServerProxy().setEnabled(false);
            settingDb.getApiProxy().getCloudProxy().setEnabled(false);
            break;
          case SERVER_PROXY:
            settingDb.getApiProxy().getNoProxy().setEnabled(false);
            settingDb.getApiProxy().getClientProxy().setEnabled(false);
            settingDb.getApiProxy().getServerProxy().setEnabled(true);
            settingDb.getApiProxy().getCloudProxy().setEnabled(false);
            break;
          case CLOUD_PROXY:
            settingDb.getApiProxy().getNoProxy().setEnabled(false);
            settingDb.getApiProxy().getClientProxy().setEnabled(false);
            settingDb.getApiProxy().getServerProxy().setEnabled(false);
            settingDb.getApiProxy().getCloudProxy().setEnabled(true);
            break;
          default:
            // NOOP
        }
        settingUserRepo.save(settingDb);
        return null;
      }
    }.execute();
  }

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
            // NOOP
        }
        settingDb.setSocialBind(socialBinding);
        settingUserRepo.save(settingDb);
        return null;
      }
    }.execute();
  }

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
            // NOOP
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
