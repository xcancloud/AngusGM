package cloud.xcan.angus.api.commonlink.tenant.cert;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BID_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
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
public class PersonalCert implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Personal user name.",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_BID_LENGTH)
  @Schema(description = "Personal user ID Card No.",
      maxLength = MAX_BID_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String certNo;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The front url of personal user ID card.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String certFrontPicUrl;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The back url of personal user ID card.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String certBackPicUrl;

}
