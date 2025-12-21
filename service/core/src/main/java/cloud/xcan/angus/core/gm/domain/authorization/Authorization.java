package cloud.xcan.angus.core.gm.domain.authenticationorization;

import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Authorization domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_authorization")
public class Authorization extends TenantAuditingEntity<Authorization, Long> {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "subject_type", length = 20)
  private SubjectType subjectType;

  @Column(name = "subject_id", nullable = false)
  private Long subjectId;

  @Column(name = "policy_id", nullable = false)
  private Long policyId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private AuthorizationStatus status;

  @Column(name = "valid_from")
  private LocalDateTime validFrom;

  @Column(name = "valid_to")
  private LocalDateTime validTo;

  @Column(name = "description", length = 500)
  private String description;

  // Non-persistent fields
  @Transient
  private String subjectName;

  @Transient
  private String policyName;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Authorization authorization)) {
      return false;
    }
    return Objects.equals(id, authorization.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
