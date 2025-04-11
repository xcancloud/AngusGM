package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WebTagSearch {

  Page<WebTag> search(Set<SearchCriteria> criteria, Pageable pageable, Class<WebTag> appTagClass,
      String... matches);
}
