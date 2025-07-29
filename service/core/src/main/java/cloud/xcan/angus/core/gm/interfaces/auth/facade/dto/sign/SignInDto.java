package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;

import cloud.xcan.angus.api.enums.SignInType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class SignInDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth2 client identifier", example = "xcan_tp", requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "OAuth2 client secret", example = "6917ae827c964acc8dd7638fe0581b67", requiredMode = RequiredMode.REQUIRED)
  private String clientSecret;

  @NotNull
  @Schema(description = "User sign-in authentication type", requiredMode = RequiredMode.REQUIRED)
  private SignInType signinType;

  @Schema(description = "User identifier for sign-in. Higher priority than account, required when multiple accounts exist")
  private Long userId;

  /**
   * Note: The password possible is linkSecret value
   */
  @NotBlank
  @Length(max = MAX_LINK_SECRET_LENGTH)
  @Schema(description = "User password or SMS/email verification link secret", requiredMode = RequiredMode.REQUIRED)
  private String password;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Login account (username, mobile number, or email). Must specify either account or userId")
  private String account;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "OAuth2 scope specifying granted permissions for resource access. Multiple values separated by commas",
      requiredMode = RequiredMode.REQUIRED, example = "user_trust")
  private String scope;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Current user sign-in device identifier", example = "068dfcaaff5db4dbc4a40bfe24cfad2b")
  private String deviceId;

}
