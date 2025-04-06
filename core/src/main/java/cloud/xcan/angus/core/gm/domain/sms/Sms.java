package cloud.xcan.angus.core.gm.domain.sms;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "sms")
@Setter
@Getter
@Accessors(chain = true)
public class Sms extends EntitySupport<Sms, Long> {

  @Id
  private Long id;

  @Column(name = "template_code")
  private String templateCode;

  @Enumerated(EnumType.STRING)
  private SupportedLanguage language;

  @Column(name = "biz_key")
  @Enumerated(EnumType.STRING)
  private SmsBizKey bizKey;

  /**
   * External business assurance ID is unique
   */
  @Column(name = "out_id")
  private String outId;

  @Column(name = "third_input_param")
  private String thirdInputParam;

  @Column(name = "third_output_param")
  private String thirdOutputParam;

  @Type(JsonType.class)
  @Column(name = "input_param_data", columnDefinition = "json")
  private InputParam inputParamData;

  /**
   * The verification code only supports SendByMobiles type
   */
  @Column(name = "verification_code")
  private Boolean verificationCode;

  private Boolean batch;

  @Column(name = "send_tenant_id")
  private Long sendTenantId;

  @Column(name = "send_user_id")
  private Long sendUserId;

  private Boolean urgent;

  @Column(name = "send_status")
  @Enumerated(EnumType.STRING)
  private ProcessStatus sendStatus;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "send_retry_num")
  private Integer sendRetryNum;

  @Column(name = "actual_send_date")
  private LocalDateTime actualSendDate;

  @Column(name = "expected_send_date")
  private LocalDateTime expectedSendDate;

  @Transient
  private Long channelId;
  @Transient
  private ReceiveObjectType receiveObjectType;
  @Transient
  private List<Long> receiveObjectIds;
  @Transient
  private List<String> receivePolicyCodes;

  /**
   * High priority
   */
  public boolean isSendByMobiles() {
    return nonNull(inputParamData) && isNotEmpty(inputParamData.getMobiles());
  }

  /**
   * Low priority
   */
  public boolean isSendByOrgType() {
    return nonNull(receiveObjectType) && (isNotEmpty(receiveObjectIds)
        || isNotEmpty(receivePolicyCodes));
  }

  public boolean isSendNow() {
    return urgent || (nonNull(expectedSendDate) && expectedSendDate.isBefore(LocalDateTime.now()));
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
