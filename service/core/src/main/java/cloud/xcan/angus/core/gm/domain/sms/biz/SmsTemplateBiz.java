package cloud.xcan.angus.core.gm.domain.sms.biz;


import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
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
@Table(name = "sms_template_biz")
@Setter
@Getter
@Accessors(chain = true)
public class SmsTemplateBiz extends EntitySupport<SmsTemplateBiz, SmsBizKey> {

  @Id
  @Column(name = "biz_key")
  @Enumerated(EnumType.STRING)
  private SmsBizKey bizKey;

  @Column(name = "template_code")
  private String templateCode;

  @Override
  public SmsBizKey identity() {
    return this.bizKey;
  }

}
