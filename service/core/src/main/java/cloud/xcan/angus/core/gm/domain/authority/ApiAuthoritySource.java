package cloud.xcan.angus.core.gm.domain.authority;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

@Getter
public enum ApiAuthoritySource implements Value<String> {
  /**
   * Permissions for public application interfaces or interface permissions required to enter the
   * application home page.
   */
  APP,

  /**
   * Interface permissions required for a given function (menu, button, panel).
   */
  APP_FUNC,

  /**
   * System token authorization interface permissions.
   */
  SYSTEM_TOKEN;

  @Override
  public String getValue() {
    return this.name();
  }
}
