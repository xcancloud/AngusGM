package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
public class CurrentEmailUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Email business type key.", requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @Email
  @NotBlank
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "User email.", example = "james@xcan.cloud",
      maxLength = MAX_EMAIL_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String email;

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Email verification code.", example = "778717",
      maxLength = MAX_VERIFICATION_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

  @NotBlank
  @Length(max = MAX_LINK_SECRET_LENGTH)
  @Schema(description = "Email linkSecret.", maxLength = MAX_LINK_SECRET_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String linkSecret;

}
