package cloud.xcan.angus.core.gm.application.query.api;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApiSearch {

  Page<Api> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Api> apiClass, String... matches);
}
