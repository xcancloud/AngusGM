package cloud.xcan.angus.api.gm.tenant.dto.cert;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class BusinessRecognizeDto {

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "URL address of license file used for identification.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String businessLicensePicUrl;

}
