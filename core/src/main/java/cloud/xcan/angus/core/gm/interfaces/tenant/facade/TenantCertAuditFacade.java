package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameAuditDto;
import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameSubmitDto;
import cloud.xcan.angus.api.gm.tenant.vo.audit.TenantAuditDetailVo;


public interface TenantCertAuditFacade {

  void authSubmit(TenantRealNameSubmitDto dto);

  void audit(TenantRealNameAuditDto dto);

  TenantAuditDetailVo detail();

  void check();
}
