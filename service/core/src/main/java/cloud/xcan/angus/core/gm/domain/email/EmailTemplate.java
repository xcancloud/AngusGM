package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * Email template domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_email_template")
public class EmailTemplate extends TenantAuditingEntity<EmailTemplate, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Column(name = "type", length = 50)
  private String type;

  @Column(name = "subject", nullable = false, length = 200)
  private String subject;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Type(JsonType.class)
  @Column(name = "params", columnDefinition = "json")
  private List<String> params;

  @Column(name = "status", length = 20)
  private String status;

  @Transient
  private Long usageCount;

  @Transient
  private Double openRate;

  @Transient
  private Double clickRate;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EmailTemplate template)) {
      return false;
    }
    return Objects.equals(id, template.id)
        && Objects.equals(code, template.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}

