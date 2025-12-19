package cloud.xcan.angus.core.gm.infra.persistence.mysql.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantRepo;
import org.springframework.stereotype.Repository;

/**
 * MySQL implementation of TenantRepo
 */
@Repository
public interface TenantRepoMysql extends TenantRepo {
  // Spring Data JPA will automatically implement all methods from TenantRepo
}
