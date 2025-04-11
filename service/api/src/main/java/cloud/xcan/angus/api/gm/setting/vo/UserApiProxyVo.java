package cloud.xcan.angus.api.gm.setting.vo;

import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.ApiProxy;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserApiProxyVo implements Serializable {

  @Schema(description = "Configuration without proxy.")
  private ApiProxy noProxy;

  @Schema(description = "Configuration of client proxy.")
  private ApiProxy clientProxy;

  @Schema(description = "Configuration of server proxy.")
  private ApiProxy serverProxy;

  @Schema(description = "Configuration of cloud proxy.")
  private ApiProxy cloudProxy;

}
