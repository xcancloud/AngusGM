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
  @Schema(description = "Display name for the email template")
  private String name;

  @Length(max = MAX_EMAIL_CONTEXT_SIZE)
  @Schema(description = "Email template content body")
  private String content;

  @Schema(description = "Whether this is a verification code email template")
  private Boolean verificationCode;

  @Schema(description = "Verification code validity period in seconds")
  private Integer verificationCodeValidSecond;

  @Length(max = MAX_SUBJECT_PREFIX_LENGTH)
  @Schema(description = "Prefix to be added to email subject lines")
  private String subjectPrefix;

  @Schema(description = "Email template language setting")
  private SupportedLanguage language;

}
