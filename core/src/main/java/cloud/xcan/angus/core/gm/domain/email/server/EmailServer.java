package cloud.xcan.angus.core.gm.domain.email.server;

import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "email_server")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class EmailServer extends AuditingEntity<EmailTemplate, Long> {

  @Id
  private Long id;

  private String name;

  /**
   * @see EmailProtocol
   */
  @Enumerated(EnumType.STRING)
  private EmailProtocol protocol;

  private String remark;

  private Boolean enabled;

  private String host;

  private Integer port;

  @Column(name = "start_tls_enabled")
  private Boolean startTlsEnabled;

  @Column(name = "ssl_enabled")
  private Boolean sslEnabled = true;

  @Column(name = "auth_enabled")
  private Boolean authEnabled;

  @Type(JsonType.class)
  @Column(name = "auth_account_data", columnDefinition = "json")
  private AuthAccount authAccountData;

  @Column(name = "subject_prefix")
  private String subjectPrefix;

  public boolean isValidSmtpServer() {
    return Objects.nonNull(enabled) && enabled && EmailProtocol.SMTP.equals(protocol);
  }

  @Override
  public Long identity() {
    return this.id;
  }

}
