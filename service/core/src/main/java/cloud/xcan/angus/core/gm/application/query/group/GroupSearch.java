package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupSearch {

  Page<Group> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Group> clz,
      String... matches);
}
