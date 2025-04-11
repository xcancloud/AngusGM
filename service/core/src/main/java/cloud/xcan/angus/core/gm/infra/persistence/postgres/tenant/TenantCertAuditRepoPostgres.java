package cloud.xcan.angus.core.gm.infra.persistence.postgres.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAuditRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantCertAuditRepoPostgres extends TenantCertAuditRepo {

}
