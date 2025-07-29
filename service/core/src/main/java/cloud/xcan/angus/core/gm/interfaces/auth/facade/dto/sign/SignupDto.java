package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.commonlink.user.SignupType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class SignupDto {

  @NotNull
  @Schema(description = "User registration type", requiredMode = RequiredMode.REQUIRED)
  private SignupType signupType;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description =
      "User registration account (mobile number or email). After registration, user can login using email, "
          + "mobile, or username assigned by backend", requiredMode = RequiredMode.REQUIRED)
  private String account;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone country code", example = "86", maxLength = MAX_ITC_LENGTH)
  private String itc;

  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "Country code for user location", example = "CN",
      maxLength = MAX_KEY_LENGTH)
  private String country;

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Mobile or email verification code for registration. Valid for 5 minutes by default",
      example = "778717", requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

  /**
   * Note: The password possible is linkSecret value.
   */
  @NotBlank
  @Length(max = MAX_LINK_SECRET_LENGTH)
  @Schema(description = "User registration password for system login",
      example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String password;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Platform invitation code for registering user to existing tenant account",
      maxLength = MAX_CODE_LENGTH)
  private String invitationCode;

}
