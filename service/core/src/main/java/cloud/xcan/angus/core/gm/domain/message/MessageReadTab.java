package cloud.xcan.angus.core.gm.domain.message;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum MessageReadTab implements EnumMessage<String> {
  READ,
  UNREAD,
  ALL;

  @Override
  public String getValue() {
    return this.name();
  }

}
