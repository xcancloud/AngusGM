package cloud.xcan.angus.api.commonlink.tag;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "org_tag_target")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class OrgTagTarget extends TenantEntity<OrgTagTarget, Long> implements Serializable {

  @Id
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type")
  private OrgTargetType targetType;

  @Column(name = "tag_id")
  private Long tagId;

  @Column(name = "target_id")
  private Long targetId;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  protected Long createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Transient
  private OrgTag tag;

  @Transient
  private String tagName;
  @Transient
  private String targetName;
  @Transient
  private Long targetCreatedBy;
  @Transient
  private LocalDateTime targetCreatedDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrgTagTarget tagTarget = (OrgTagTarget) o;
    return tagId.equals(tagTarget.tagId) && targetId.equals(tagTarget.targetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId, targetId);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
