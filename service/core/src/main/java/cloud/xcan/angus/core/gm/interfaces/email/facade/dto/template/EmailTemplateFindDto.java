package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template;

import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EmailTemplateFindDto extends PageQuery {

  private Long id;

  private String code;

  private String name;

  private Boolean verificationCode;

  private String subject;

  private SupportedLanguage language;

}
