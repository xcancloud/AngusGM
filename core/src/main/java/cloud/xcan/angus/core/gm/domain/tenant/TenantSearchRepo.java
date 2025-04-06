package cloud.xcan.angus.core.gm.domain.tenant;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TenantSearchRepo extends CustomBaseRepository<Tenant> {

}
