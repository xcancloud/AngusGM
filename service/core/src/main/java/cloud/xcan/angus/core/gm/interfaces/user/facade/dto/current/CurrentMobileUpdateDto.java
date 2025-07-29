package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BID_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.validator.EnumPart;
import cloud.xcan.angus.validator.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
public class CurrentMobileUpdateDto implements Serializable {

  @NotNull
  @EnumPart(enumClass = SmsBizKey.class, allowableValues = {"MODIFY_MOBILE"})
  @Schema(description = "SMS verification business type for mobile number update", example = "MODIFY_MOBILE",
      allowableValues = {"MODIFY_MOBILE"}, requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Mobile
  @NotBlank
  @Schema(description = "New mobile phone number to update to", example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "Country code for the new mobile number", example = "CN", requiredMode = RequiredMode.REQUIRED)
  private String country;

  @NotBlank
  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone country code", example = "86", requiredMode = RequiredMode.REQUIRED)
  private String itc;

  @Length(max = MAX_BID_LENGTH)
  @Schema(description = "SMS verification code sent to the new mobile number", example = "778717", requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

  @NotBlank
  @Length(max = MAX_LINK_SECRET_LENGTH)
  @Schema(description = "Security token for SMS verification process", requiredMode = RequiredMode.REQUIRED)
  private String linkSecret;

}
