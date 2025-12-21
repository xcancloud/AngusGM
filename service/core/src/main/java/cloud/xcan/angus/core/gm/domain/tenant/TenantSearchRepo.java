package cloud.xcan.angus.core.gm.domain.tenant;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Tenant full-text search repository interface
 */
@NoRepositoryBean
public interface TenantSearchRepo extends CustomBaseRepository<Tenant> {

}
