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
  @Schema(description = "Sms business type key.", example = "MODIFY_MOBILE",
      allowableValues = {"MODIFY_MOBILE", "BIND_MOBILE",
          "PASSWORD_UPDATE"}, requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @NotBlank
  @Mobile
  @Schema(description = "User mobile.", example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code.", example = "CN", requiredMode = RequiredMode.REQUIRED)
  private String country;

}
