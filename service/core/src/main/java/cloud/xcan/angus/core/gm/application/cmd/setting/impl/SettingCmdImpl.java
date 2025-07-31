package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.api.commonlink.setting.SettingKey.AI_AGENT;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.API_LOG_CONFIG;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.MAX_METRICS_DAYS;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.MAX_RESOURCE_ACTIVITIES;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.OPERATION_LOG_CONFIG;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.SOCIAL;
import static cloud.xcan.angus.api.commonlink.setting.SettingKey.SYSTEM_LOG_CONFIG;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.OperationLogConverter.toModifiedOperation;
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
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.gm.domain.operation.ModifiedResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of system setting command operations.
 * </p>
 * <p>
 * Manages global system settings including social configurations, AI agent settings,
 * log configurations, and resource quotas.
 * </p>
 * <p>
 * Provides centralized setting management with proper permission validation
 * and cache eviction for real-time updates.
 * </p>
 */
@Biz
@Slf4j
public class SettingCmdImpl extends CommCmd<Setting, Long> implements SettingCmd {

  @Resource
  private SettingRepo settingRepo;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Updates system settings with validation and cache management.
   * </p>
   * <p>
   * Validates permissions for setting modifications and updates the database.
   * Automatically evicts related cache entries to ensure real-time updates.
   * </p>
   * <p>
   * Supports various setting types including social, AI agent, logging,
   * and resource quota configurations.
   * </p>
   */
  @CacheEvict(key = "'key_' + #key", value = "setting")
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(SettingKey key, Setting setting) {
    new BizTemplate<Void>() {

      @Override
      protected void checkParams() {
        // Verify permissions for setting modifications
        assertTrue(isPrivateEdition() || (isCloudServiceEdition() && isOpClient()),
            "Only allow privatization or cloud service edition modification on the operation client");
      }

      @Override
      protected Void process() {
        switch (setting.getKey()) {
          case SOCIAL:
            // Update social configuration settings
            settingRepo.updateValueByKey(SOCIAL.getValue(), toJson(setting.getSocial()));
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.SOCIAL));
            return null;
          case AI_AGENT:
            // Update AI agent configuration settings
            settingRepo.updateValueByKey(AI_AGENT.getValue(), toJson(setting.getAiAgent()));
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.AI_AGENT));
            return null;
          case OPERATION_LOG_CONFIG:
            // Update operation log configuration settings
            settingRepo.updateValueByKey(OPERATION_LOG_CONFIG.getValue(),
                toJson(setting.getOperationLog()));
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.OPERATION_LOG));
            return null;
          case API_LOG_CONFIG:
            // Update API log configuration settings
            settingRepo.updateValueByKey(API_LOG_CONFIG.getValue(), toJson(setting.getApiLog()));
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.API_LOG));
            return null;
          case SYSTEM_LOG_CONFIG:
            // Update system log configuration settings
            settingRepo.updateValueByKey(SYSTEM_LOG_CONFIG.getValue(),
                toJson(setting.getSystemLog()));
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.SYSTEM_LOG));
            return null;
          case MAX_RESOURCE_ACTIVITIES:
            // Update maximum resource activities setting
            settingRepo.updateValueByKey(MAX_RESOURCE_ACTIVITIES.getValue(),
                setting.getMaxResourceActivities().toString());
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.MAX_RESOURCE_ACTIVITIES));
            return null;
          case MAX_METRICS_DAYS:
            // Update maximum metrics days setting
            settingRepo.updateValueByKey(MAX_METRICS_DAYS.getValue(),
                setting.getMaxMetricsDays().toString());
            operationLogCmd.add(toModifiedOperation(setting.getKey().getValue(),
                "setting", true, ModifiedResourceType.MAX_METRICS_DAYS));
            return null;
          default: {
            // NOOP for unsupported setting types
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
