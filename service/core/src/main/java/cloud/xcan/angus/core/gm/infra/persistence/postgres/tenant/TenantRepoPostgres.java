package cloud.xcan.angus.core.gm.infra.persistence.postgres.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantRepo;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of TenantRepo
 */
@Repository
public interface TenantRepoPostgres extends TenantRepo {
  // Spring Data JPA will automatically implement all methods from TenantRepo
}
