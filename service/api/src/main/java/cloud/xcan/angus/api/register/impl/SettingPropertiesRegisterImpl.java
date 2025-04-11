package cloud.xcan.angus.api.register.impl;


import static cloud.xcan.angus.remote.ApiConstant.Service.COMMON_SERVICE;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.api.register.SettingPropertiesRegister;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SettingPropertiesRegisterImpl implements SettingPropertiesRegister {

  @Resource
  private SettingManager settingManager;

  @Resource
  private OperationLogProperties operationLogProperties;

  @Resource
  private ApiLogProperties apiLogProperties;

  @Resource
  private SystemLogProperties systemLogProperties;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  public boolean support() {
    String commonLinkEnabled = SpringContextHolder.getCtx().getEnvironment()
        .getProperty("xcan.datasource.commonlink.enabled");
    boolean support = COMMON_SERVICE.equalsIgnoreCase(applicationInfo.getInstanceId())
        || "true".equalsIgnoreCase(commonLinkEnabled);
    if (!support) {
      log.warn("Detected that common-link datasource is not enabled, "
          + "and the setting properties registration is ignored");
    } else {
      log.info("Detected that common-link datasource is enabled, "
          + "and the setting properties will be registered");
    }
    return support;
  }

  @Override
  public void register() {
    settingManager.setting(SettingKey.OPERATION_LOG_CONFIG).getOperationLog().register();
    settingManager.setting(SettingKey.API_LOG_CONFIG).getApiLog().register();
    settingManager.setting(SettingKey.SYSTEM_LOG_CONFIG).getSystemLog().register();
  }

  @Override
  public OperationLogProperties getRefreshedOperationLogProperties() {
    // Refresh system properties
    settingManager.setting(SettingKey.OPERATION_LOG_CONFIG).getOperationLog().register();
    return operationLogProperties;
  }

  @Override
  public ApiLogProperties getRefreshedApiLogProperties() {
    // Refresh system properties
    settingManager.setting(SettingKey.API_LOG_CONFIG).getApiLog().register();
    return apiLogProperties;
  }

  @Override
  public SystemLogProperties getRefreshedSystemLogProperties() {
    // Refresh system properties
    settingManager.setting(SettingKey.SYSTEM_LOG_CONFIG).getSystemLog().register();
    return systemLogProperties;
  }
}
