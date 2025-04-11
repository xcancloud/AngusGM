package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EmailDetailVo implements Serializable {

  private Long id;

  private String templateCode;

  private SupportedLanguage language;

  private EmailBizKey bizKey;

  private String outId;

  private String sendId;

  private EmailType emailType;

  private String subject;

  private String fromAddr;

  private Boolean verificationCode;

  private Integer verificationCodeValidSecond;

  private Set<String> toAddress;

  private Set<String> ccAddress;

  private Boolean html;

  private ProcessStatus sendStatus;

  private String failureReason;

  private String content;

  private Map<String, Map<String, String>> templateParams;

  private Set<Attachment> attachments;

  private LocalDateTime expectedSendDate;

  private LocalDateTime actualSendDate;

  private Boolean urgent;

  private Long sendTenantId;

  private Boolean batch;

  private LocalDateTime createdDate;

}
