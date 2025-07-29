package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class SmsTemplateUpdateDto implements Serializable {

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "SMS template display name for identification", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "SMS template content with dynamic variables", example = "content")
  private String content;

  @Schema(description = "Verification code SMS template flag for code-based authentication",
      requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode;

  @Schema(description = "Verification code validity period in seconds", example = "300")
  private Integer verificationCodeValidSecond;

  /**
   * Only allow to modify third code, modifying the code will cause system errors.
   */
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Third-party SMS template identifier code (system code modification not allowed)", maxLength = MAX_CODE_LENGTH)
  private String thirdCode;

  @Schema(description = "SMS template language for multi-language support")
  private SupportedLanguage language;

}
