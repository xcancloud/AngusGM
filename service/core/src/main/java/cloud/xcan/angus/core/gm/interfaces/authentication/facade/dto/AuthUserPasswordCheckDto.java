package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

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
public class AuthUserPasswordCheckDto {

  @NotNull
  @Schema(description = "User identifier for password verification", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Password()
  @Schema(description = "Password value to verify", example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String password;

}
