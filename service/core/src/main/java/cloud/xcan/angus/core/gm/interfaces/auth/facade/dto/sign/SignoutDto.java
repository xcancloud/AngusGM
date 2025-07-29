package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
public class SignoutDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth2 client identifier", example = "xcan_tp", requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "OAuth2 client secret", example = "6917ae827c964acc8dd7638fe0581b67", requiredMode = RequiredMode.REQUIRED)
  private String clientSecret;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "Access token to invalidate during logout", requiredMode = RequiredMode.REQUIRED)
  private String accessToken;

}
