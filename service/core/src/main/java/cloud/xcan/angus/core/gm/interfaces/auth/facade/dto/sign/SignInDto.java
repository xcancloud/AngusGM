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
  @Schema(description = "OAuh2 client id", example = "xcan_tp",
      maxLength = MAX_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "OAuh2 client secret", example = "6917ae827c964acc8dd7638fe0581b67",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String clientSecret;

  @NotNull
  @Schema(description = "User sign-in (login) type", requiredMode = RequiredMode.REQUIRED)
  private SignInType signinType;

  @Schema(description = "Sign-in user ID, priority is higher than account, "
      + "the login user ID must be specified when there are multiple accounts")
  private Long userId;

  /**
   * Note: The password possible is linkSecret value
   */
  @NotBlank
  @Length(max = MAX_LINK_SECRET_LENGTH)
  @Schema(description = "Account login password or SMS/email-based login secure link",
      requiredMode = RequiredMode.REQUIRED)
  private String password;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Login account. Can be username, mobile or email. "
      + "The account and userId must specify one", maxLength = MAX_CODE_LENGTH)
  private String account;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "The scope field specifies the permissions granted to the client, "
      + "limiting resource access. Note: Multiple values are separated by commas",
      requiredMode = RequiredMode.REQUIRED, example = "user_trust", maxLength = MAX_CODE_LENGTH_X5)
  private String scope;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Current user sign-in device id",
      example = "068dfcaaff5db4dbc4a40bfe24cfad2b", maxLength = MAX_CODE_LENGTH)
  private String deviceId;

}
