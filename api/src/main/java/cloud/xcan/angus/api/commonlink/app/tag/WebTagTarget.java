package cloud.xcan.angus.api.commonlink.app.tag;

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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "web_tag_target")
@Setter
@Getter
@Accessors(chain = true)
@EntityListeners({AuditingEntityListener.class})
public class WebTagTarget extends TenantEntity<WebTagTarget, Long> {

  @Id
  private Long id;

  @Column(name = "tag_id")
  private Long tagId;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type")
  private WebTagTargetType targetType;

  @Column(name = "target_id")
  private Long targetId;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  private Long createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  @Transient
  private WebTag tag;
  @Transient
  private String tagName;
  @Transient
  private String targetName;
  @Transient
  private Long targetCreatedBy;
  @Transient
  private LocalDateTime targetCreatedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
