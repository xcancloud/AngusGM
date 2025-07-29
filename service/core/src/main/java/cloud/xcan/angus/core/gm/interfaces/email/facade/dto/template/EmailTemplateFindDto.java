package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template;

import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EmailTemplateFindDto extends PageQuery {

  @Schema(description = "Email template identifier")
  private Long id;

  @Schema(description = "Email template code for filtering")
  private String code;

  @Schema(description = "Email template name for filtering")
  private String name;

  @Schema(description = "Whether this is a verification code email template")
  private Boolean verificationCode;

  @Schema(description = "Email subject for filtering")
  private String subject;

  @Schema(description = "Email template language for filtering")
  private SupportedLanguage language;

}
