package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

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
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class CurrentMobileCheckDto implements Serializable {

  @NotNull
  @EnumPart(enumClass = SmsBizKey.class, allowableValues = {
      "PASSWORD_FORGET", "PASSWORD_UPDATE", "SIGN_CANCEL", "MODIFY_MOBILE", "BIND_MOBILE"
  })
  @Schema(description = "Sms business type key.", example = "MODIFY_MOBILE",
      allowableValues = {
          "PASSWORD_FORGET", "PASSWORD_UPDATE", "SIGN_CANCEL", "MODIFY_MOBILE", "BIND_MOBILE"
      }, requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @NotBlank
  @Mobile
  @Schema(description = "User mobile.", example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code.", example = "CN", maxLength = MAX_COUNTRY_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String country;

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Sms verification code.", example = "778717", maxLength = MAX_VERIFICATION_CODE_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
