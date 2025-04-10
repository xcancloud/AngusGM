package cloud.xcan.angus.api.commonlink.tenant;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.TenantSource;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.spec.experimental.Resources;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "tenant")
@Setter
@Getter
@Accessors(chain = true)
public class Tenant extends AuditingEntity<Tenant, Long> implements Resources {

  @Id
  private Long id;

  private String name;

  private String no;

  @Enumerated(EnumType.STRING)
  private TenantType type;

  @Enumerated(EnumType.STRING)
  private TenantSource source;

  @Enumerated(EnumType.STRING)
  private TenantStatus status;

  @Column(name = "apply_cancel_date")
  private LocalDateTime applyCancelDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "real_name_status")
  private TenantRealNameStatus realNameStatus;

  private String address;

  private Boolean locked;

  @Column(name = "last_lock_date")
  private LocalDateTime lastLockDate;

  @Column(name = "lock_start_date")
  private LocalDateTime lockStartDate;

  @Column(name = "lock_end_date")
  private LocalDateTime lockEndDate;

  private String remark;

  @Transient
  private Long userCount;

  public boolean isRealNamePassed() {
    return nonNull(realNameStatus) && realNameStatus.equals(TenantRealNameStatus.AUDITED);
  }

  public boolean isRealNameAuditing() {
    return !nonNull(realNameStatus) || !realNameStatus.equals(TenantRealNameStatus.AUDITED);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
