package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.enums.HttpMethod;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * Interface domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_interface")
public class Interface extends TenantAuditingEntity<Interface, Long> {

  @Id
  private Long id;

  @Column(name = "service_id", nullable = false)
  private Long serviceId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 100, unique = true)
  private String code;

  @Column(name = "path", nullable = false, length = 255)
  private String path;

  @Enumerated(EnumType.STRING)
  @Column(name = "method", length = 20)
  private HttpMethod method;

  @Column(name = "summary", length = 200)
  private String summary;

  @Column(name = "description", length = 500)
  private String description;

  @Type(JsonType.class)
  @Column(name = "tags", columnDefinition = "json")
  private List<String> tags;

  @Column(name = "version", length = 20)
  private String version;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private InterfaceStatus status;

  @Column(name = "require_auth")
  private Boolean requireAuth;

  @Column(name = "deprecated")
  private Boolean deprecated;

  @Column(name = "deprecation_note", length = 500)
  private String deprecationNote;

  @Column(name = "last_sync_time")
  private LocalDateTime lastSyncTime;

  @Type(JsonType.class)
  @Column(name = "parameters", columnDefinition = "json")
  private List<Map<String, Object>> parameters;

  @Type(JsonType.class)
  @Column(name = "responses", columnDefinition = "json")
  private Map<String, Object> responses;

  // Non-persistent fields
  @Transient
  private String serviceName;

  @Transient
  private Long policyCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Interface interface_)) {
      return false;
    }
    return Objects.equals(id, interface_.id)
        && Objects.equals(code, interface_.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
