package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_CONTEXT_SIZE;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_SUBJECT_PREFIX_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class EmailTemplateUpdateDto implements Serializable {

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Template name.", maxLength = MAX_NAME_LENGTH)
  private String name;

  @Length(max = MAX_EMAIL_CONTEXT_SIZE)
  @Schema(description = "Template content.", maxLength = MAX_EMAIL_CONTEXT_SIZE)
  private String content;

  @Schema(description = "Whether or not verification code email flag.")
  private Boolean verificationCode;

  @Schema(description = "Validity of verification code, in seconds.")
  private Integer verificationCodeValidSecond;

  @Length(max = MAX_SUBJECT_PREFIX_LENGTH)
  @Schema(description = "Template email subject prefix.", maxLength = MAX_SUBJECT_PREFIX_LENGTH)
  private String subjectPrefix;

  @Schema(description = "Template email language.")
  private SupportedLanguage language;

}
