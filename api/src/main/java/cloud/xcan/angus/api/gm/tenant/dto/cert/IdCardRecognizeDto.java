package cloud.xcan.angus.api.gm.tenant.dto.cert;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class IdCardRecognizeDto implements Serializable {

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "URL address of personal ID face picture used for identification.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String facePicUrl;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "URL address of personal ID background picture used for identification.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String backPicUrl;

}
