package cloud.xcan.angus.core.gm.domain.event.push;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum EventPushStatus implements EnumMessage<String> {

  PENDING, PUSHING, PUSH_SUCCESS, PUSH_FAIL, IGNORED;

  @Override
  public String getValue() {
    return this.name();
  }

}
