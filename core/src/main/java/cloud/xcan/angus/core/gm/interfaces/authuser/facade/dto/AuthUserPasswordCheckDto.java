package cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto;

import cloud.xcan.angus.validator.Passd;
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
  @Schema(description = "Check password user id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Passd()
  @Schema(description = "Check password value.", example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String password;

}
