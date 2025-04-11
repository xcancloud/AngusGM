package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user;

import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.ApiProxyType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserApiProxyEnabledDto implements Serializable {

  @NotNull
  @Schema(description = "Enable apis proxy name.", requiredMode = RequiredMode.REQUIRED)
  private ApiProxyType name;

}
