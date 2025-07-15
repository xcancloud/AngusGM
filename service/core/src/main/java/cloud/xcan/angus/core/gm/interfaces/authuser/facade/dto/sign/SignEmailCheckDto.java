package cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign;

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
  @Schema(description = "Sign email business types", example = "SIGNIN", requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @Email
  @NotEmpty
  @Schema(description = "Email verification code receiving address", requiredMode = RequiredMode.REQUIRED)
  private String email;

  @NotEmpty
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Email verification code", example = "897261",
      maxLength = MAX_VERIFICATION_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
