package cloud.xcan.angus.core.gm.domain.policy;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Policy domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_policy")
public class Policy extends TenantAuditingEntity<Policy, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "effect", length = 20)
  private PolicyEffect effect;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private PolicyStatus status;

  @Column(name = "resource_ids", columnDefinition = "TEXT")
  private String resourceIds; // JSON array of interface IDs

  @Column(name = "priority")
  private Integer priority;

  // Non-persistent fields
  @Transient
  private List<Long> resourceIdList;

  @Transient
  private Long authorizationCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Policy policy)) {
      return false;
    }
    return Objects.equals(id, policy.id)
        && Objects.equals(code, policy.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
