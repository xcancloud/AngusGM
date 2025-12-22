package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Email SMTP configuration domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_email_smtp")
public class EmailSmtp extends TenantAuditingEntity<EmailSmtp, Long> {

  @Id
  private Long id;

  @Column(name = "host", nullable = false, length = 200)
  private String host;

  @Column(name = "port", nullable = false)
  private Integer port;

  @Column(name = "username", nullable = false, length = 100)
  private String username;

  @Column(name = "password", length = 255)
  private String password;

  @Column(name = "from_name", length = 100)
  private String fromName;

  @Column(name = "from_email", nullable = false, length = 100)
  private String fromEmail;

  @Column(name = "use_ssl")
  private Boolean useSsl = true;

  @Column(name = "is_default")
  private Boolean isDefault = false;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EmailSmtp smtp)) {
      return false;
    }
    return Objects.equals(id, smtp.id)
        && Objects.equals(host, smtp.host);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, host);
  }
}

