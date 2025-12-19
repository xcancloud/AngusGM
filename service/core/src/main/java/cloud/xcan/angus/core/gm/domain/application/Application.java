package cloud.xcan.angus.core.gm.domain.application;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Application domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_application")
public class Application extends TenantAuditingEntity<Application, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20)
  private ApplicationType type;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "home_url", length = 255)
  private String homeUrl;

  @Column(name = "redirect_url", length = 255)
  private String redirectUrl;

  @Column(name = "client_id", length = 100)
  private String clientId;

  @Column(name = "client_secret", length = 255)
  private String clientSecret;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private ApplicationStatus status;

  @Column(name = "owner_id")
  private Long ownerId;

  // Non-persistent fields
  @Transient
  private String ownerName;

  @Transient
  private Long serviceCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Application application)) {
      return false;
    }
    return Objects.equals(id, application.id)
        && Objects.equals(code, application.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
