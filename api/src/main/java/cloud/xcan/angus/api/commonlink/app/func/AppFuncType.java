package cloud.xcan.angus.api.commonlink.app.func;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum AppFuncType implements EnumMessage<String> {
  MENU,
  BUTTON,
  PANEL;

  @Override
  public String getValue() {
    return this.name();
  }

  public WebTagTargetType toTagTargetType() {
    return switch (this) {
      case MENU -> WebTagTargetType.MENU;
      case BUTTON -> WebTagTargetType.BUTTON;
      case PANEL -> WebTagTargetType.PANEL;
    };
  }
}
