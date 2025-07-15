package cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign;

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
  @Schema(description = "OAuh2 client id", example = "xcan_tp",
      maxLength = MAX_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "OAuh2 client secret", example = "6917ae827c964acc8dd7638fe0581b67",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String clientSecret;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The access token that needs to be invalidated out when logout",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String accessToken;

}
