package cloud.xcan.angus.api.gm.indicator;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;

@EndpointRegister
public enum SmokeCheckSetting implements EnumMessage<String> {

  SERVICE_AVAILABLE,
  API_AVAILABLE,
  USER_DEFINED_ASSERTION;

  @Override
  public String getValue() {
    return this.name();
  }
}
