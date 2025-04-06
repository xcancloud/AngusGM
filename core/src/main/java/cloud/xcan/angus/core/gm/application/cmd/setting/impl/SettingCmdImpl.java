package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.api.commonlink.setting.SettingKey.AI_AGENT;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.API_LOG_CONFIG;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.HEALTH_CHECK;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.MAX_METRICS_DAYS;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.MAX_RESOURCE_ACTIVITIES;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.OPERATION_LOG_CONFIG;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.SOCIAL;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.SYSTEM_LOG_CONFIG;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.utils.GsonUtils.toJson;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isPrivateEdition;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.SettingRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;


@Biz
@Slf4j
public class SettingCmdImpl extends CommCmd<Setting, Long> implements SettingCmd {

  @Resource
  private SettingRepo settingRepo;

  @CacheEvict(key = "'key_' + #key", value = "setting")
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(SettingKey key, Setting setting) {
    new BizTemplate<Void>() {

      @Override
      protected void checkParams() {
        assertTrue(isPrivateEdition() || (isCloudServiceEdition() && isOpClient()),
            "Only allow privatization or cloud service edition modification on the operation client");
      }

      @Override
      protected Void process() {
        switch (setting.getKey()) {
          case SOCIAL:
            settingRepo.updateValueByKey(SOCIAL.getValue(), toJson(setting.getSocial()));
            return null;
          case HEALTH_CHECK:
            settingRepo.updateValueByKey(HEALTH_CHECK.getValue(), toJson(setting.getHealthCheck()));
            return null;
          case AI_AGENT:
            settingRepo.updateValueByKey(AI_AGENT.getValue(), toJson(setting.getAiAgent()));
            return null;
          case OPERATION_LOG_CONFIG:
            settingRepo.updateValueByKey(OPERATION_LOG_CONFIG.getValue(),
                toJson(setting.getOperationLog()));
            return null;
          case API_LOG_CONFIG:
            settingRepo.updateValueByKey(API_LOG_CONFIG.getValue(), toJson(setting.getApiLog()));
            return null;
          case SYSTEM_LOG_CONFIG:
            settingRepo.updateValueByKey(SYSTEM_LOG_CONFIG.getValue(),
                toJson(setting.getSystemLog()));
            return null;
          case MAX_RESOURCE_ACTIVITIES:
            settingRepo.updateValueByKey(MAX_RESOURCE_ACTIVITIES.getValue(),
                setting.getMaxResourceActivities().toString());
          case MAX_METRICS_DAYS:
            settingRepo.updateValueByKey(MAX_METRICS_DAYS.getValue(),
                setting.getMaxMetricsDays().toString());
          default: {
            // NOOP
          }
          return null;
        }
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Setting, Long> getRepository() {
    return settingRepo;
  }
}
