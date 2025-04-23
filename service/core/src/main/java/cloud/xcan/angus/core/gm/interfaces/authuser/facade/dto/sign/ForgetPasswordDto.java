package cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;

import cloud.xcan.angus.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class ForgetPasswordDto {

  @NotNull
  @Schema(description = "Forget password user id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotEmpty
  @Length(max = MAX_LINK_SECRET_LENGTH)
  @Schema(description = "Forget password user sms or email linkSecret.", requiredMode = RequiredMode.REQUIRED)
  private String linkSecret;

  @Password
  @Schema(description = "User new password.", example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String newPassword;

}
