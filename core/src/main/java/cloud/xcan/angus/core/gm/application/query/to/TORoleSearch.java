package cloud.xcan.angus.core.gm.application.query.to;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TORoleSearch {

  Page<TORole> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<TORole> topPolicyClass, String... matches);
}
