package cloud.xcan.angus.api.gm.sms.dto;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.validator.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class SmsVerificationCodeCheckDto implements Serializable {

  @NotNull
  @Schema(description = "Sms business key.", example = "SIGNUP", requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Mobile
  @NotEmpty
  @Schema(description = "Verification mobile.", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotEmpty
  @Length(max = 6)
  @Schema(example = "Sms verification code.", maxLength = 6, requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
