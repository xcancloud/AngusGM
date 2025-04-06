package cloud.xcan.angus.api.commonlink.policy;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@EndpointRegister
@Getter
public enum PolicyGrantStage implements EnumMessage<String> {

  MANUAL,
  SIGNUP_SUCCESS
  /*,REAL_NAME_PASSED,*/
  /*OPEN_SUCCESS*/;

  public boolean isManual() {
    return this.equals(MANUAL);
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
