package cloud.xcan.angus.api.commonlink.setting.user.apiproxy;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserApiProxy extends ValueObjectSupport<UserApiProxy> {

  @Schema(description = "Configuration without proxy.")
  private ApiProxy noProxy;

  @Schema(description = "Configuration of client proxy.")
  private ApiProxy clientProxy;

  @Schema(description = "Configuration of server proxy.")
  private ApiProxy serverProxy;

  @Schema(description = "Configuration of cloud proxy.")
  private ApiProxy cloudProxy;

  @Override
  public UserApiProxy copy() {
    return new UserApiProxy(noProxy, clientProxy, serverProxy, cloudProxy);
  }
}
