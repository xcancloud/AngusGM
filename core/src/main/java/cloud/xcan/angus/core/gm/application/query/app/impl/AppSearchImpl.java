package cloud.xcan.angus.core.gm.application.query.app.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppSearch;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class AppSearchImpl implements AppSearch {

  @Resource
  private AppSearchRepo appSearchRepo;

  @Override
  public Page<App> search(Set<SearchCriteria> criteria, Pageable pageable, Class<App> clazz,
      String... matches) {
    return new BizTemplate<Page<App>>() {

      @Override
      protected Page<App> process() {
        return appSearchRepo.find(criteria, pageable, clazz, matches);
      }
    }.execute();
  }

}
