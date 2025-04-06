package cloud.xcan.angus.core.gm.domain.app;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AppListRepo extends CustomBaseRepository<App> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria, Class<App> mainClz,
      String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
