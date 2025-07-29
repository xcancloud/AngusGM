package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.validator.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SignCancelSmsDto implements Serializable {

  @NotBlank
  @Mobile
  @Schema(description = "Mobile phone number for tenant account deletion verification", example = "13877897201", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotNull
  @Schema(description = "SMS verification business type for tenant account deletion", example = "SIGN_CANCEL", allowableValues = {
      "SIGN_CANCEL"}, requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

}
