package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
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
 * Tag domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_tag")
public class Tag extends TenantAuditingEntity<Tag, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "color", length = 20)
  private String color;

  @Column(name = "category", length = 50)
  private String category;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private TagStatus status;

  @Column(name = "sort_order")
  private Integer sortOrder;

  // Non-persistent fields
  @Transient
  private Long usageCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tag tag)) {
      return false;
    }
    return Objects.equals(id, tag.id)
        && Objects.equals(name, tag.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
