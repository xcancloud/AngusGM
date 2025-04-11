package cloud.xcan.angus.core.gm.application.cmd.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;

public interface TenantCertAuditCmd {

  Void submit(TenantCertAudit tenantAudit);

  void audit(TenantCertAudit tenantAudits);

  void check();

}
