package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.spec.utils.JsonUtils.convert;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.SettingRepo;
import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.social.Social;
import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.commonlink.setting.tenant.healthcheck.HealthCheck;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
@Biz
public class SettingManagerImpl implements SettingManager {

  @Autowired(required = false)
  private SettingRepo settingRepo;

  @Autowired(required = false)
  private SettingManager settingManager;

  @Override
  public Setting setting(SettingKey key) {
    Setting setting = settingManager.getCachedSetting(key);
    try {
      return switch (key) {
        case LOCALE -> setting.setLocale(convert(setting.getValue(), Locale.class));
        case SECURITY -> setting.setSecurity(convert(setting.getValue(),
            Security.class));
        case SOCIAL -> setting.setSocial(convert(setting.getValue(),
            Social.class));
        case QUOTA -> setting.setQuota(convert(setting.getValue(),
            new TypeReference<List<Quota>>() {
            }));
        case TESTER_EVENT -> setting.setTesterEvent(convert(setting.getValue(),
            new TypeReference<List<TesterEvent>>() {
            }));
        case AI_AGENT -> setting.setAiAgent(convert(setting.getValue(), AIAgent.class));
        case FUNC_INDICATOR -> setting.setFunc(convert(setting.getValue(), Func.class));
        case PREF_INDICATOR -> setting.setPerf(convert(setting.getValue(), Perf.class));
        case STABILITY_INDICATOR ->
            setting.setStability(convert(setting.getValue(), Stability.class));
        case HEALTH_CHECK -> setting.setHealthCheck(convert(setting.getValue(), HealthCheck.class));
        case OPERATION_LOG_CONFIG ->
            setting.setOperationLog(convert(setting.getValue(), OperationLogProperties.class));
        case API_LOG_CONFIG ->
            setting.setApiLog(convert(setting.getValue(), ApiLogProperties.class));
        case SYSTEM_LOG_CONFIG ->
            setting.setSystemLog(convert(setting.getValue(), SystemLogProperties.class));
        case MAX_RESOURCE_ACTIVITIES ->
            setting.setMaxResourceActivities(isNotEmpty(setting.getValue())
                ? Integer.parseInt(setting.getValue().trim()) : null);
        case MAX_METRICS_DAYS -> setting.setMaxMetricsDays(isNotEmpty(setting.getValue())
            ? Integer.parseInt(setting.getValue().trim()) : null);
      };
    } catch (JsonProcessingException e) {
      log.error("Parse setting json exception:", e);
      throw SysException.of("Parse setting json exception:" + e.getMessage());
    }
  }

  @Override
  @Cacheable(key = "'key_' + #key", value = "setting")
  public Setting getCachedSetting(SettingKey key) {
    return settingRepo.findByKey(key)
        .orElseThrow(() -> ResourceNotFound.of(key.getValue(), "Setting"));
  }

}
