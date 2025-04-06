package cloud.xcan.angus.core.gm.application.query.to;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TOUserSearch {

  Page<TOUser> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<TOUser> topUserClass, String... matches);
}
