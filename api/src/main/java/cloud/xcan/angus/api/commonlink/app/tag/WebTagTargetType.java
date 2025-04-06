package cloud.xcan.angus.api.commonlink.app.tag;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.spec.ValueObject;
import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;


@Getter
public enum WebTagTargetType implements ValueObject<WebTagTargetType>, Value<String> {

  APP, MENU, BUTTON, PANEL;

  @Override
  public String getValue() {
    return this.name();
  }

  public AppFuncType toAppFuncType() {
    return switch (this) {
      case MENU -> AppFuncType.MENU;
      case BUTTON -> AppFuncType.BUTTON;
      case PANEL -> AppFuncType.PANEL;
      default -> null;
    };
  }

  public static boolean isAppFuncType(String type) {
    return type.equals(MENU.getValue()) || type.equals(BUTTON.getValue())
        || type.equals(PANEL.getValue());
  }
}
