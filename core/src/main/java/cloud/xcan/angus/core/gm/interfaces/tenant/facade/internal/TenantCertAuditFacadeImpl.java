package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAuditAssembler.auditDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAuditAssembler.submitDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAuditAssembler.toDetailVo;

import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameAuditDto;
import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameSubmitDto;
import cloud.xcan.angus.api.gm.tenant.vo.audit.TenantAuditDetailVo;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCertAuditCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertAuditQuery;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantCertAuditFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class TenantCertAuditFacadeImpl implements TenantCertAuditFacade {

  @Resource
  private TenantCertAuditCmd tenantCertAuditCmd;

  @Resource
  private TenantCertAuditQuery tenantCertAuditQuery;

  @Override
  public void authSubmit(TenantRealNameSubmitDto dto) {
    tenantCertAuditCmd.submit(submitDtoToDomain(dto));
  }

  @Override
  public void audit(TenantRealNameAuditDto dto) {
    tenantCertAuditCmd.audit(auditDtoToDomain(dto));
  }

  @Override
  public void check() {
    tenantCertAuditCmd.check();
  }

  @Override
  public TenantAuditDetailVo detail() {
    return toDetailVo(tenantCertAuditQuery.detail());
  }

}
