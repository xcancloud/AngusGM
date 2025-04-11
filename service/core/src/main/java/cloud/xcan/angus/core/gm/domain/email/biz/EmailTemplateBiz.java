package cloud.xcan.angus.core.gm.domain.email.biz;


import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.spec.experimental.EntitySupport;
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
@Table(name = "email_template_biz")
@Setter
@Getter
@Accessors(chain = true)
public class EmailTemplateBiz extends EntitySupport<EmailTemplateBiz, EmailBizKey> {

  /**
   * Business key
   *
   * @see cloud.xcan.angus.core.biz.BizTemplate#bizKey
   */
  @Id
  @Column(name = "biz_key")
  @Enumerated(EnumType.STRING)
  private EmailBizKey bizKey;

  /**
   * template_code
   */
  @Column(name = "template_code")
  private String templateCode;

  @Override
  public EmailBizKey identity() {
    return this.bizKey;
  }

}
