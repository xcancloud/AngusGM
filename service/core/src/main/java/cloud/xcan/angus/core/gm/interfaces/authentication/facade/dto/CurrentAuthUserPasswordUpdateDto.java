package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import cloud.xcan.angus.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CurrentAuthUserPasswordUpdateDto {

  @Password(allowNull = false)
  @Schema(description = "Current user password for verification", example = "123@5678_", requiredMode = RequiredMode.REQUIRED)
  private String oldPassword;

  @Password(allowNull = false)
  @Schema(description = "New password for current user account", example = "876@4321_", requiredMode = RequiredMode.REQUIRED)
  private String newPassword;

}
