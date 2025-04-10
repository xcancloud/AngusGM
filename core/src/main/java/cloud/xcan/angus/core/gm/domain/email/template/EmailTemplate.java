package cloud.xcan.angus.core.gm.domain.email.template;


import cloud.xcan.angus.core.gm.domain.email.biz.EmailTemplateBiz;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import cloud.xcan.angus.spec.experimental.Resources;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "email_template")
@Setter
@Getter
@Accessors(chain = true)
public class EmailTemplate extends EntitySupport<EmailTemplate, Long> implements Resources<Long> {

  @Id
  private Long id;

  private String code;

  private String name;

  private String content;

  private String subject;

  @Enumerated(EnumType.STRING)
  private SupportedLanguage language;

  @Column(name = "verification_code")
  private Boolean verificationCode;

  @Column(name = "verification_code_valid_second")
  private Integer verificationCodeValidSecond;

  @Transient
  private EmailTemplateBiz templateBiz;

  @Override
  public Long identity() {
    return this.id;
  }
}
