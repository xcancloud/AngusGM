package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Tenant full-text search repository MySQL implementation
 */
@Repository
public class TenantSearchRepoMysql extends SimpleSearchRepository<Tenant>
    implements TenantSearchRepo {

}
