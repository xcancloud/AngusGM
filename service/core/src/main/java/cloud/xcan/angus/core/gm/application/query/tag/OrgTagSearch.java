package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrgTagSearch {


  Page<OrgTag> search(Set<SearchCriteria> criteria, Pageable pageable, Class<OrgTag> clz,
      String... matches);
}
