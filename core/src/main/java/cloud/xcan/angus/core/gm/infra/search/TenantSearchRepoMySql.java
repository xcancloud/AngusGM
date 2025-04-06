package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TenantSearchRepoMySql extends SimpleSearchRepository<Tenant> implements
    TenantSearchRepo {

}
