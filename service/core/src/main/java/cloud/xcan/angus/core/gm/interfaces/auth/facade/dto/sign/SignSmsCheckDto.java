package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.validator.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class SignSmsCheckDto {

  @NotNull
  @Schema(description = "Sign sms business types", example = "SIGNIN", requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Mobile
  @NotEmpty
  @Schema(description = "Sms verification code receiving mobile", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotEmpty
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Sms verification code", maxLength = MAX_VERIFICATION_CODE_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
