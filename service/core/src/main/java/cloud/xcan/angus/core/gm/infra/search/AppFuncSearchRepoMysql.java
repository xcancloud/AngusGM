package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncListRepo;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncSearchRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class AppFuncSearchRepoMysql extends AbstractSearchRepository<AppFunc>
    implements AppFuncSearchRepo {

  @Resource
  private AppFuncListRepo userListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<AppFunc> mainClz,
      Object[] params, String... matches) {
    return userListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz, "app_func", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return userListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }

}
