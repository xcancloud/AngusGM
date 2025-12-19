package cloud.xcan.angus.core.gm.domain.tenant;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Tenant domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_tenant")
public class Tenant extends TenantAuditingEntity<Tenant, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Column(name = "type", length = 20)
  private String type;

  @Column(name = "account_type", length = 20)
  private String accountType;

  @Column(name = "admin_name", length = 50)
  private String adminName;

  @Column(name = "admin_email", length = 100)
  private String adminEmail;

  @Column(name = "admin_phone", length = 20)
  private String adminPhone;

  @Column(name = "address", length = 500)
  private String address;

  @Column(name = "expire_date")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate expireDate;

  @Column(name = "status", length = 20)
  private String status;

  @Column(name = "logo", length = 500)
  private String logo;

  // Non-persistent fields - for temporary associated data
  @Transient
  private Long userCount;

  @Transient
  private Long departmentCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tenant tenant)) {
      return false;
    }
    return Objects.equals(id, tenant.id)
        && Objects.equals(code, tenant.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
