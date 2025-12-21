package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class SignEmailCheckDto {

  @NotNull
  @Schema(description = "Email verification business type", example = "SIGNIN", requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @Email
  @NotEmpty
  @Schema(description = "Email address for verification code delivery", requiredMode = RequiredMode.REQUIRED)
  private String email;

  @NotEmpty
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Email verification code", example = "897261", requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
