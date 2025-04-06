package cloud.xcan.angus.core.gm.domain;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum SentType implements EnumMessage<String> {

  SEND_NOW,

  TIMING_SEND;

  @Override
  public String getValue() {
    return this.name();
  }
}
