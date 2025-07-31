package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.api.commonlink.CommonConstant.BID_INVITATION_CODE_KEY;
import static cloud.xcan.angus.core.gm.application.converter.OperationLogConverter.toModifiedOperation;
import static cloud.xcan.angus.core.gm.application.converter.SettingTenantConverter.initTenantSetting;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;
import cloud.xcan.angus.api.commonlink.setting.tenant.apiproxy.ServerApiProxy;
import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.gm.domain.operation.ModifiedResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.idgen.BidGenerator;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant setting command operations.
 * </p>
 * <p>
 * Manages tenant-level settings including locale, security, API proxy,
 * and performance indicator configurations.
 * </p>
 * <p>
 * Provides tenant setting initialization, updates, and cache management
 * for real-time configuration updates.
 * </p>
 */
@Biz
@Slf4j
public class SettingTenantCmdImpl extends CommCmd<SettingTenant, Long> implements SettingTenantCmd {

  @Resource
  private SettingTenantRepo settingTenantRepo;
  @Resource
  private SettingTenantQuery settingTenantQuery;
  @Resource
  private SettingQuery settingQuery;
  @Resource
  private BidGenerator bidGenerator;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Replaces tenant locale settings.
   * </p>
   * <p>
   * Updates tenant locale configuration and evicts related cache entries
   * to ensure real-time updates across the system.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void localeReplace(Locale locale) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant settingDb = settingTenantQuery.find(getOptTenantId());
        settingDb.setLocaleData(locale);
        updateTenantSetting(getOptTenantId(), settingDb);

        operationLogCmd.add(toModifiedOperation("LOCALE",
            "SettingTenant", true, ModifiedResourceType.TENANT_LOCALE));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant security settings.
   * </p>
   * <p>
   * Updates security configuration including invitation code settings
   * and logs the modification for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void securityReplace(Security security) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant setting = settingTenantQuery.find(getOptTenantId());
        setting.setInvitationCode(Objects.isNull(security.getSignupAllow())
            ? null : security.getSignupAllow().getInvitationCode());
        setting.setSecurityData(security);
        updateTenantSetting(getOptTenantId(), setting);

        operationLogCmd.add(toModifiedOperation("SECURITY",
            "SettingTenant", true, ModifiedResourceType.TENANT_SECURITY));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Generates invitation code for tenant.
   * </p>
   * <p>
   * Creates a unique invitation code using bid generator and random string
   * for tenant invitation purposes.
   * </p>
   */
  @Override
  public String invitationCodeGen() {
    return new BizTemplate<String>() {

      @Override
      protected String process() {
        String code = bidGenerator.getId(BID_INVITATION_CODE_KEY) + "." + randomAlphanumeric(6);

        operationLogCmd.add(toModifiedOperation("INVITATION_CODE",
            "SettingTenant", true, ModifiedResourceType.TENANT_INVITATION_CODE));
        return code;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant API proxy settings.
   * </p>
   * <p>
   * Updates server API proxy configuration for the tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void proxyReplace(ServerApiProxy apiProxy) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant setting = settingTenantQuery.find(getOptTenantId());
        setting.setServerApiProxyData(apiProxy);
        updateTenantSetting(getOptTenantId(), setting);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant tester event settings.
   * </p>
   * <p>
   * Updates tester event configuration for the tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void testerEventReplace(List<TesterEvent> testerEvent) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant setting = settingTenantQuery.find(getOptTenantId());
        setting.setTesterEventData(testerEvent);
        updateTenantSetting(getOptTenantId(), setting);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant function indicator settings.
   * </p>
   * <p>
   * Updates function performance indicator configuration for the tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void funcReplace(Func data) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant setting = settingTenantQuery.find(getOptTenantId());
        setting.setFuncData(data);
        updateTenantSetting(getOptTenantId(), setting);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant performance indicator settings.
   * </p>
   * <p>
   * Updates performance indicator configuration for the tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void perfReplace(Perf data) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant setting = settingTenantQuery.find(getOptTenantId());
        setting.setPerfData(data);
        updateTenantSetting(getOptTenantId(), setting);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant stability indicator settings.
   * </p>
   * <p>
   * Updates stability indicator configuration for the tenant.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void stabilityReplace(Stability stability) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant setting = settingTenantQuery.find(getOptTenantId());
        setting.setStabilityData(stability);
        updateTenantSetting(getOptTenantId(), setting);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates tenant setting with cache eviction.
   * </p>
   * <p>
   * Saves tenant setting to database and evicts related cache entries
   * to ensure real-time updates across the system.
   * </p>
   */
  @CacheEvict(key = "'key_' + #tenantId", value = "settingTenant")
  public void updateTenantSetting(Long tenantId, SettingTenant setting) {
    settingTenantRepo.save(setting);
  }

  /**
   * <p>
   * Initializes tenant settings.
   * </p>
   * <p>
   * Creates default tenant settings with timezone configuration
   * when tenant settings don't exist.
   * </p>
   */
  @Override
  public void init(Long tenantId) {
    if (!settingTenantRepo.existsByTenantId(tenantId)) {
      SettingTenant tenantSetting = initTenantSetting(tenantId, settingQuery);
      // Load timezone from application configuration
      tenantSetting.getLocaleData().setDefaultTimeZone(getApplicationInfo().getTimezone());
      settingTenantRepo.save(tenantSetting);
    }
  }

  @Override
  protected BaseRepository<SettingTenant, Long> getRepository() {
    return this.settingTenantRepo;
  }
}
