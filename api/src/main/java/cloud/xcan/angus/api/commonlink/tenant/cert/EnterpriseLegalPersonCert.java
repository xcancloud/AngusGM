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
public class EnterpriseLegalPersonCert implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Enterprise legal person name.",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_BID_LENGTH)
  @Schema(description = "The ID card no of enterprise legal person.",
      maxLength = MAX_BID_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String certNo;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The ID card front url of enterprise legal person.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String certFrontPicUrl;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "The ID card back url of enterprise legal person.",
      maxLength = MAX_URL_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String certBackPicUrl;

}
