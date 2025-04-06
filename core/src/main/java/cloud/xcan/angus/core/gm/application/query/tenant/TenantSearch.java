package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TenantSearch {

  Page<Tenant> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Tenant> clz,
      String... matches);

}
