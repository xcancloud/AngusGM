package cloud.xcan.angus.core.gm.domain.email;


import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "email")
@Setter
@Getter
@Accessors(chain = true)
public class Email extends AuditingEntity<Email, Long> {

  @Id
  private Long id;

  @Column(name = "out_id")
  private String outId;

  @Column(name = "template_code")
  private String templateCode;

  @Enumerated(EnumType.STRING)
  private SupportedLanguage language;

  @Column(name = "biz_key")
  @Enumerated(EnumType.STRING)
  private EmailBizKey bizKey;

  /**
   * @see EmailType#TEMPLATE Send after formatting the template
   * @see EmailType#CUSTOM Send the original text
   */
  @Enumerated(EnumType.STRING)
  private EmailType type;

  @Column
  private String subject;

  @Column(name = "from_addr")
  private String fromAddr;

  @Type(JsonType.class)
  @Column(name = "to_addr_data", columnDefinition = "json")
  private Set<String> toAddrData;

  @Type(JsonType.class)
  @Column(name = "cc_addr_data", columnDefinition = "json")
  private Set<String> ccAddrData;

  /**
   * Whether verification code.
   */
  @Column(name = "verification_code")
  private Boolean verificationCode;

  @Column(name = "verification_code_valid_second")
  private Integer verificationCodeValidSecond;

  /**
   * Whether html email.
   */
  private Boolean html;

  /**
   * @see ProcessStatus
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "send_status")
  private ProcessStatus sendStatus;

  @Column(name = "send_retry_num")
  private Integer sendRetryNum;

  @Column(name = "failure_reason")
  private String failureReason;

  /**
   * The content of the email
   */
  @Column
  private String content;

  /**
   * EmailTemplateCmd email parameters
   */
  @Type(JsonType.class)
  @Column(name = "template_param_data", columnDefinition = "json")
  private Map<String, Map<String, String>> templateParamData;

  /**
   * Email attachments
   */
  @Type(JsonType.class)
  @Column(name = "attachment_data", columnDefinition = "json")
  private Set<Attachment> attachmentData;

  /**
   * Expected sending time
   */
  @Column(name = "expected_send_date")
  private LocalDateTime expectedSendDate;

  /**
   * Actual sending time
   */
  @Column(name = "actual_send_date")
  private LocalDateTime actualSendDate;

  /**
   * Whether the email needs urgent processing, if it is, it will be sent first
   */
  private Boolean urgent;

  /**
   * Send tenant ID
   */
  @Column(name = "send_tenant_id")
  private Long sendTenantId;

  /**
   * Send user ID
   */
  @Column(name = "send_user_id")
  private Long sendUserId;

  @Column(name = "receive_object_type")
  @Enumerated(EnumType.STRING)
  private ReceiveObjectType receiveObjectType;

  /**
   * If true `to` will display multiple addresses.
   */
  private Boolean batch;

  @Transient
  private List<Long> receiveObjectIds;
  @Transient
  private List<String> receivePolicyCodes;
  @Transient
  private Long serverId;
  @Transient
  private Set<String> actualToAddrData;

  public boolean isTemplateEmail() {
    return nonNull(type) && type.equals(EmailType.TEMPLATE);
  }

  /**
   * High priority
   */
  public boolean isSendByToAddress() {
    return isNotEmpty(toAddrData);
  }

  /**
   * Low priority
   */
  public boolean isSendByOrgType() {
    return nonNull(receiveObjectType) && (receiveObjectType.equals(ReceiveObjectType.ALL)
        || isNotEmpty(receiveObjectIds) || isNotEmpty(receivePolicyCodes));
  }

  /**
   * Important :: All platform users must send one email to one address
   */
  public boolean isSendByAllPlatformUsers() {
    return Objects.nonNull(receiveObjectType) && receiveObjectType.equals(ReceiveObjectType.ALL);
  }

  public boolean isSendNow() {
    return (urgent || (nonNull(expectedSendDate)
        && expectedSendDate.isBefore(LocalDateTime.now())) /*&& !batch*/);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
