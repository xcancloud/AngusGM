package cloud.xcan.angus.core.gm.application.query.dept;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeptSearch {

  Page<Dept> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Dept> clz,
      String... matches);
}
