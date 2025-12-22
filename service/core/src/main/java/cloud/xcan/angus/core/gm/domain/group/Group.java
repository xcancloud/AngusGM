package cloud.xcan.angus.core.gm.domain.group;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
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
 * Group domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_group")
public class Group extends TenantAuditingEntity<Group, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20)
  private GroupType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private GroupStatus status;

  @Column(name = "owner_id")
  private Long ownerId;

  // Non-persistent fields
  @Transient
  private Long memberCount;

  @Transient
  private String ownerName;

  @Transient
  private String ownerAvatar;

  @Transient
  private List<Long> memberIds;

  @Transient
  private List<String> tags;

  @Transient
  private java.time.LocalDateTime lastActive;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Group group)) {
      return false;
    }
    return Objects.equals(id, group.id)
        && Objects.equals(code, group.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
