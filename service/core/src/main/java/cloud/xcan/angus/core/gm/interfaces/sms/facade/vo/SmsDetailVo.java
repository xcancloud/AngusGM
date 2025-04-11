package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter

@Accessors(chain = true)
public class SmsDetailVo implements Serializable {

  private Long id;

  private String templateCode;

  private SupportedLanguage language;

  private SmsBizKey bizKey;

  private String outId;

  private String thirdOutputParam;

  private SmsInputParamVo inputParam;

  private Boolean verificationCode;

  private Boolean batch;

  private Long sendTenantId;

  private Long sendUserId;

  private Boolean urgent;

  private ProcessStatus sendStatus;

  private LocalDateTime actualSendDate;

  private LocalDateTime expectedSendDate;

}
