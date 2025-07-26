package cloud.xcan.angus.core.gm.domain.tenant.audit;

import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseLegalPersonCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.GovernmentCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.PersonalCert;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "tenant_cert_audit")
@Setter
@Getter
@Accessors(chain = true)
public class TenantCertAudit extends TenantAuditingEntity<TenantCertAudit, Long> {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  private TenantRealNameStatus status;

  @Enumerated(EnumType.STRING)
  private TenantType type;

  @Type(JsonType.class)
  @Column(name = "personal_cert_data", columnDefinition = "json")
  private PersonalCert personalCertData;

  @Type(JsonType.class)
  @Column(name = "enterprise_cert_data", columnDefinition = "json")
  private EnterpriseCert enterpriseCertData;

  @Type(JsonType.class)
  @Column(name = "enterprise_legal_personcert_data", columnDefinition = "json")
  private EnterpriseLegalPersonCert enterpriseLegalPersonCertData;

  @Type(JsonType.class)
  @Column(name = "government_cert_data", columnDefinition = "json")
  private GovernmentCert governmentCertData;

  @Column(name = "auto_audit")
  private Boolean autoAudit;

  @Type(JsonType.class)
  @Column(name = "audit_record_data", columnDefinition = "json")
  private AuditRecordData auditRecordData;

  public boolean isRealNamePassed() {
    return Objects.nonNull(status) && status.equals(TenantRealNameStatus.AUDITED);
  }

  public boolean isRealNameFailed() {
    return Objects.nonNull(status) && status.equals(TenantRealNameStatus.FAILED_AUDIT);
  }

  public boolean isRealNameAuditing() {
    return Objects.nonNull(status) && status.equals(TenantRealNameStatus.AUDITING);
  }

  public boolean isCertSubmitted() {
    if (Objects.nonNull(type)) {
      return switch (type) {
        case GOVERNMENT -> Objects.nonNull(governmentCertData);
        case ENTERPRISE -> Objects.nonNull(enterpriseCertData)
            && Objects.nonNull(enterpriseLegalPersonCertData);
        case PERSONAL -> Objects.nonNull(personalCertData);
        default -> false;
      };
    }
    return false;
  }

  public String getTenantName() {
    if (Objects.nonNull(type)) {
      switch (type) {
        case GOVERNMENT:
          return Objects.nonNull(governmentCertData) ? governmentCertData.getName() : null;
        case ENTERPRISE:
          return Objects.nonNull(enterpriseCertData) ? enterpriseCertData.getName() : null;
        case PERSONAL:
          return Objects.nonNull(personalCertData) ? personalCertData.getName() : null;
        default:
          return null;
      }
    }
    return null;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
