package cloud.xcan.angus.api.gm.indicator;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;

@EndpointRegister
public enum SecurityCheckSetting implements EnumMessage<String> {
  NOT_SECURITY_CODE,
  IS_SECURITY_CODE,
  USER_DEFINED_ASSERTION;

  @Override
  public String getValue() {
    return this.name();
  }
}
