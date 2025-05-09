package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class CurrentEmailCheckDto implements Serializable {

  @NotNull
  @EnumPart(enumClass = EmailBizKey.class, allowableValues = {
      "PASSWORD_FORGET", "PASSWORD_UPDATE", "MODIFY_EMAIL", "BIND_EMAIL"
  })
  @Schema(description = "Email business type key.", example = "BIND_EMAIL",
      allowableValues = {
          "PASSWORD_FORGET", "PASSWORD_UPDATE", "MODIFY_EMAIL", "BIND_EMAIL"
      }, requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @Email
  @NotBlank
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "User e-mail.", example = "james@xcan.cloud", maxLength = MAX_EMAIL_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String email;

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Email verification code.", example = "778717", maxLength = MAX_VERIFICATION_CODE_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
