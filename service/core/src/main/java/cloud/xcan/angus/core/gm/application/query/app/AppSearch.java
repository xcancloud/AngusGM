package cloud.xcan.angus.core.gm.application.query.app;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AppSearch {

  Page<App> search(Set<SearchCriteria> criteria, Pageable pageable, Class<App> clazz,
      String... matches);

}
