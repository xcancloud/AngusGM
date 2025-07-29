package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

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
public class UserSysAdminSetDto implements Serializable {

  @NotNull
  @Schema(description = "Unique identifier of the user account", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "System administrator privilege status", example = "false", requiredMode = RequiredMode.REQUIRED)
  private Boolean sysAdmin = false;

}

