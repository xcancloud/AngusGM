package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto;

import cloud.xcan.angus.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AuthUserPasswordUpdateDto {

  @NotNull
  @Schema(description = "User identifier for password update", example = "100", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Password
  @NotNull
  @Schema(description = "New password for user account", example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String newPassword;

}
