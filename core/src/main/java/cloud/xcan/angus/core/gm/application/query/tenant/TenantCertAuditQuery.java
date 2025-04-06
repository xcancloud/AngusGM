package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;

public interface TenantCertAuditQuery {

  TenantCertAudit detail();

  TenantCertAudit find0(Long id);

  TenantCertAudit checkAndFind(Long id);

  TenantCertAudit checkAndFindByTenantId(Long tenantId);

  TenantCertAudit findByTenantId(Long tenantId);
}
