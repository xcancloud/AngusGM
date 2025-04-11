package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.template;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SmsTemplateDetailVo implements Serializable {

  private Long id;

  private String code;

  private String name;

  private Long channelId;

  private String signature;

  private String content;

  private String thirdCode;

  private SupportedLanguage language;

  private Boolean verificationCode;

  private Integer verificationCodeValidSecond;

}
