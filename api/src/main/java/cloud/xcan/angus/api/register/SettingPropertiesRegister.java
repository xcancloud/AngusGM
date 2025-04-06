package cloud.xcan.angus.api.register;

import cloud.xcan.angus.core.app.AppPropertiesRegister;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;

public interface SettingPropertiesRegister extends AppPropertiesRegister {

  OperationLogProperties getRefreshedOperationLogProperties();

  ApiLogProperties getRefreshedApiLogProperties();

  SystemLogProperties getRefreshedSystemLogProperties();

}
