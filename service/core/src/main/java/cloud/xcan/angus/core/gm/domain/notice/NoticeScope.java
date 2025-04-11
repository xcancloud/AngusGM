package cloud.xcan.angus.core.gm.domain.notice;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum NoticeScope implements EnumMessage<String> {
  GLOBAL,
  APP;

  @Override
  public String getValue() {
    return this.name();
  }

}
