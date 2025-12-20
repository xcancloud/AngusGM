package cloud.xcan.angus.core.gm.domain.service;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
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
 * Service domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_service")
public class Service extends TenantAuditingEntity<Service, Long> {

  @Id
  private Long id;

  @Column(name = "application_id", nullable = false)
  private Long applicationId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "protocol", length = 20)
  private ServiceProtocol protocol;

  @Column(name = "base_url", length = 255)
  private String baseUrl;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private ServiceStatus status;

  @Column(name = "version", length = 20)
  private String version;

  // Non-persistent fields
  @Transient
  private String applicationName;

  @Transient
  private Long interfaceCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Service service)) {
      return false;
    }
    return Objects.equals(id, service.id)
        && Objects.equals(code, service.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
