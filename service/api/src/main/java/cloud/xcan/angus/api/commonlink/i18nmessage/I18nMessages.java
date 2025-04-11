package cloud.xcan.angus.api.commonlink.i18nmessage;

import cloud.xcan.angus.core.biz.I18nMessage;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "c_i18n_messages")
@Accessors(chain = true)
public class I18nMessages extends EntitySupport<I18nMessages, Long> implements I18nMessage {

  @Id
  private Long id;

  private String type;

  private String language;

  @Column(name = "default_message")
  private String defaultMessage;

  @Column(name = "i18n_message")
  private String i18nMessage;

  @Override
  public Long identity() {
    return id;
  }
}
