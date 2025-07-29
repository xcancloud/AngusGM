package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto;

import cloud.xcan.angus.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CurrentAuthUserPasswordCheckDto {

  @Password(allowNull = false)
  @Schema(description = "Current user password for verification", requiredMode = RequiredMode.REQUIRED)
  private String password;

}
