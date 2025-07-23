package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.gm.tenant.dto.TenantReplaceDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantUpdateDto;
import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameAuditDto;
import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameSubmitDto;
import cloud.xcan.angus.api.gm.tenant.to.audit.AuditRecordTo;
import cloud.xcan.angus.api.gm.tenant.vo.audit.TenantAuditDetailVo;
import cloud.xcan.angus.core.gm.domain.tenant.audit.AuditRecordData;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import java.time.LocalDateTime;

public class TenantAuditAssembler {

  public static TenantCertAudit submitDtoToDomain(TenantRealNameSubmitDto dto) {
    return new TenantCertAudit()
        .setStatus(TenantRealNameStatus.AUDITING)
        .setType(dto.getType())
        .setPersonalCertData(dto.getPersonalCert())
        .setGovernmentCertData(dto.getGovernmentCert())
        .setEnterpriseCertData(dto.getEnterpriseCert())
        .setEnterpriseLegalPersonCertData(dto.getEnterpriseLegalPersonCert());
  }

  public static TenantCertAudit auditDtoToDomain(TenantRealNameAuditDto dto) {
    TenantCertAudit tenantAudit = new TenantCertAudit();
    tenantAudit.setId(dto.getId());
    tenantAudit.setStatus(dto.getStatus());
    tenantAudit.setAuditRecordData(dtoToAuditRecordDomain(dto.getReason()));
    return tenantAudit;
  }

  public static TenantCertAudit replaceDtoToAudit(TenantReplaceDto dto) {
    return new TenantCertAudit()/*.setId(id)*/
        .setType(dto.getType())
        .setGovernmentCertData(dto.getGovernmentCert())
        /*.setStatus(status)*/
        .setEnterpriseCertData(dto.getEnterpriseCert())
        .setEnterpriseLegalPersonCertData(dto.getEnterpriseLegalPersonCert())
        .setPersonalCertData(dto.getPersonalCert());
  }

  public static TenantCertAudit updateDtoToAudit(TenantUpdateDto dto) {
    return new TenantCertAudit()/*.setId(id)*/
        .setType(dto.getType())
        .setGovernmentCertData(dto.getGovernmentCert())
        /*.setStatus(status)*/
        .setEnterpriseCertData(dto.getEnterpriseCert())
        .setEnterpriseLegalPersonCertData(dto.getEnterpriseLegalPersonCert())
        .setPersonalCertData(dto.getPersonalCert());
  }

  public static TenantAuditDetailVo toDetailVo(TenantCertAudit audit) {
    if (isNull(audit)) {
      return null;
    }
    return new TenantAuditDetailVo().setId(audit.getId())
        .setType(audit.getType())
        .setGovernmentCert(audit.getGovernmentCertData())
        .setStatus(audit.getStatus())
        .setEnterpriseCert(audit.getEnterpriseCertData())
        .setEnterpriseLegalPersonCert(audit.getEnterpriseLegalPersonCertData())
        .setPersonalCert(audit.getPersonalCertData())
        .setAuditRecord(toAuditRecordTo(audit.getAuditRecordData()));
  }

  public static AuditRecordTo toAuditRecordTo(AuditRecordData data) {
    if (isNull(data)) {
      return null;
    }
    return new AuditRecordTo().setReason(data.getReason())
        .setAuditDate(data.getAuditDate())
        .setAuditUserId(data.getAuditUserId())
        .setAuditUserName(data.getAuditUserName());
  }

  public static AuditRecordData dtoToAuditRecordDomain(String reason) {
    return new AuditRecordData().setReason(reason)
        .setAuditDate(LocalDateTime.now())
        .setAuditUserId(getUserId())
        .setAuditUserName(getUserFullName());
  }

}
