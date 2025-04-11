package cloud.xcan.angus.api.commonlink.tenant.cert;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
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
public class EnterpriseCert implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Company name.", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Taxpayer identification number.", maxLength = MAX_CODE_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String creditCode;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "Business license picture url.", maxLength = MAX_URL_LENGTH_X2,
      requiredMode = RequiredMode.REQUIRED)
  private String businessLicensePicUrl;

}
