package cloud.xcan.angus.core.gm.interfaces.email.facade.vo.template;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EmailTemplateDetailVo implements Serializable {

  private Long id;

  private String code;

  private String name;

  private String content;

  private Boolean verificationCode;

  private Integer verificationCodeValidSecond;

  private String remark;

  private SupportedLanguage language;

}
