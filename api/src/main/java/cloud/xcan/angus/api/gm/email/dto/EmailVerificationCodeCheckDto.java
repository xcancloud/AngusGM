package cloud.xcan.angus.api.gm.email.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class EmailVerificationCodeCheckDto implements Serializable {

  @NotNull
  @Schema(description = "Email business key.", example = "SIGNUP", requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @Email
  @NotEmpty
  @Schema(description = "Verification email address.", requiredMode = RequiredMode.REQUIRED)
  private String email;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(example = "Email verification code.", maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
