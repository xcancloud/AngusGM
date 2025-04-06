package cloud.xcan.angus.core.gm.domain.notice;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.SentType;
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
@Table(name = "notice")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class Notice extends TenantEntity<Notice, Long> {

  @Id
  private Long id;

  private String content;

  @Enumerated(EnumType.STRING)
  private NoticeScope scope;

  @Column(name = "app_id")
  private Long appId;

  @Column(name = "send_type")
  @Enumerated(EnumType.STRING)
  private SentType sendType;

  @Column(name = "timing_date")
  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime timingDate;

  @Column(name = "expiration_date")
  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime expirationDate;

  @CreatedBy
  @Column(name = "created_by")
  private Long createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Transient
  private String appCode;
  @Transient
  private String appName;
  @Transient
  private EditionType editionType;

  public boolean isExpired() {
    return nonNull(expirationDate) && expirationDate.isBefore(LocalDateTime.now());
  }

  @Override
  public Long identity() {
    return this.id;
  }

}
