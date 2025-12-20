package cloud.xcan.angus.core.gm.domain.department;

import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
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
 * Department domain entity with tree structure support
 */
@Setter
@Getter
@Entity
@Table(name = "gm_department")
public class Department extends TenantAuditingEntity<Department, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "level")
  private Integer level;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Column(name = "leader_id")
  private Long leaderId;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private DepartmentStatus status;

  // Non-persistent fields - for temporary associated data
  @Transient
  private String leaderName;

  @Transient
  private Long memberCount;

  @Transient
  private String parentName;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Department department)) {
      return false;
    }
    return Objects.equals(id, department.id)
        && Objects.equals(code, department.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
