package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;

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
public class CurrentSmsSendDto implements Serializable {

  @NotNull
  @EnumPart(enumClass = SmsBizKey.class, allowableValues = {
      "MODIFY_MOBILE", "BIND_MOBILE", "PASSWORD_UPDATE"})
  @Schema(description = "SMS verification business type for different operations", example = "MODIFY_MOBILE",
      allowableValues = {"MODIFY_MOBILE", "BIND_MOBILE",
          "PASSWORD_UPDATE"}, requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @NotBlank
  @Mobile
  @Schema(description = "Mobile phone number to send verification code to", example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "Country code for the mobile number", example = "CN", requiredMode = RequiredMode.REQUIRED)
  private String country;

}
