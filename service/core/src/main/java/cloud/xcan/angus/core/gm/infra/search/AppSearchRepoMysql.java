package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppListRepo;
import cloud.xcan.angus.core.gm.domain.app.AppSearchRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class AppSearchRepoMysql extends AbstractSearchRepository<App> implements AppSearchRepo {

  @Resource
  private AppListRepo appListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<App> mainClz,
      Object[] params, String... matches) {
    return appListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz, "app", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return appListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }

}
