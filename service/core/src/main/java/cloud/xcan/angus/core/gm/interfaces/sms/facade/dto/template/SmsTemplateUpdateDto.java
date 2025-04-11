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
  @Schema(description = "Sms template name.", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(example = "content")
  private String content;

  @Schema(description = "Whether or not verification code sms flag.",
      requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode;

  @Schema(example = "Validity of verification code, in seconds")
  private Integer verificationCodeValidSecond;

  /**
   * Only allow to modify third code, modifying the code will cause system errors.
   */
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Third template code.", maxLength = MAX_CODE_LENGTH)
  private String thirdCode;

  @Schema(description = "Template sms language.")
  private SupportedLanguage language;

}
