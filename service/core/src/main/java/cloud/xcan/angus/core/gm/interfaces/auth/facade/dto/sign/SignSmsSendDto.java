package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;

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


@Setter
@Getter
@Accessors(chain = true)
public class SignSmsSendDto {

  @NotNull
  @Schema(description = "Sign sms business types", example = "SIGNIN", requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Mobile
  @NotEmpty
  @Schema(description = "Sms verification code receiving mobile", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Country code of the region where the phone number is located",
      example = "CN", maxLength = MAX_KEY_LENGTH)
  private String country;

}
