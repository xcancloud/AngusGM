package cloud.xcan.angus.core.gm.domain.sms.template;


import cloud.xcan.angus.spec.experimental.EntitySupport;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "sms_template")
@Setter
@Getter
@Accessors(chain = true)
public class SmsTemplate extends EntitySupport<SmsTemplate, Long> {

  @Id
  private Long id;

  /**
   * Only allow modify third code, Modifying the code will cause system errors
   */
  private String code;

  private String name;

  @Column(name = "channel_id")
  private Long channelId;

  private String signature;

  private String content;

  @Column(name = "third_code")
  private String thirdCode;

  @Enumerated(EnumType.STRING)
  private SupportedLanguage language;

  @Column(name = "verification_code")
  private Boolean verificationCode;

  @Column(name = "verification_code_valid_second")
  private Integer verificationCodeValidSecond;

  @Override
  public Long identity() {
    return this.id;
  }

}
