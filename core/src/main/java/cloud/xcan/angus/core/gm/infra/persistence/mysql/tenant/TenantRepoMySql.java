package cloud.xcan.angus.core.gm.infra.persistence.mysql.tenant;

import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import org.springframework.stereotype.Repository;

@Repository("tenantRepo")
public interface TenantRepoMySql extends TenantRepo {

}
