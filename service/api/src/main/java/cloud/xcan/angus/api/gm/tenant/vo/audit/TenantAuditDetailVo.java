package cloud.xcan.angus.api.gm.tenant.vo.audit;

import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseLegalPersonCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.GovernmentCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.PersonalCert;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.api.gm.tenant.to.audit.AuditRecordTo;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class TenantAuditDetailVo implements Serializable {

  private Long id;

  private TenantRealNameStatus status;

  private TenantType type;

  private PersonalCert personalCert;

  private EnterpriseCert enterpriseCert;

  private EnterpriseLegalPersonCert enterpriseLegalPersonCert;

  private GovernmentCert governmentCert;

  private Boolean autoAudit;

  private AuditRecordTo auditRecord;

}
