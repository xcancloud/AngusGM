package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.api.commonlink.CommonConstant.BID_INVITATION_CODE_KEY;
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
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.idgen.BidGenerator;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;


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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void localeReplace(Locale locale) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        SettingTenant settingDb = settingTenantQuery.find(getOptTenantId());
        settingDb.setLocaleData(locale);
        updateTenantSetting(getOptTenantId(), settingDb);
        return null;
      }
    }.execute();
  }

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
        return null;
      }
    }.execute();
  }

  @Override
  public String invitationCodeGen() {
    return new BizTemplate<String>() {

      @Override
      protected String process() {
        return bidGenerator.getId(BID_INVITATION_CODE_KEY) + "." + randomAlphanumeric(6);
      }
    }.execute();
  }

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

  @CacheEvict(key = "'key_' + #tenantId", value = "settingTenant")
  public void updateTenantSetting(Long tenantId, SettingTenant setting) {
    settingTenantRepo.save(setting);
  }

  @Override
  public void init(Long tenantId) {
    if (!settingTenantRepo.existsByTenantId(tenantId)) {
      SettingTenant tenantSetting = initTenantSetting(tenantId, settingQuery);
      // Load TimeZone in configuration
      tenantSetting.getLocaleData().setDefaultTimeZone(getApplicationInfo().getTimezone());
      settingTenantRepo.save(tenantSetting);
    }
  }

  @Override
  protected BaseRepository<SettingTenant, Long> getRepository() {
    return this.settingTenantRepo;
  }
}
