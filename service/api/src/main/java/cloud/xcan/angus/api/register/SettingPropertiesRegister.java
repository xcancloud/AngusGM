package cloud.xcan.angus.api.register;

import cloud.xcan.angus.core.app.AppPropertiesRegister;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;

public interface SettingPropertiesRegister extends AppPropertiesRegister {

  OperationLogProperties getRefreshedOperationLogProperties();

  ApiLogProperties getRefreshedApiLogProperties();

  SystemLogProperties getRefreshedSystemLogProperties();

  default boolean enabledOperationLog() {
    return null != getRefreshedOperationLogProperties()
        && getRefreshedOperationLogProperties().getEnabled();
  }

  default boolean enabledApiLog() {
    return null != getRefreshedApiLogProperties()
        && getRefreshedApiLogProperties().getEnabled();
  }

  default boolean enabledSystemLog() {
    return null != getRefreshedSystemLogProperties();
  }

}
