package cloud.xcan.angus.core.gm.application.query.app.impl;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncSearch;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;

@Biz
public class AppFuncSearchImpl implements AppFuncSearch {

  @Resource
  private AppFuncSearchRepo appFuncSearchRepo;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Override
  public List<AppFunc> search(Set<SearchCriteria> criteria, Class<AppFunc> clz,
      String... matches) {
    return new BizTemplate<List<AppFunc>>() {

      @Override
      protected List<AppFunc> process() {
        List<AppFunc> appFunc = appFuncSearchRepo.find(criteria, PageRequest.of(0, 10000,
            JpaSort.by(Order.asc("id"))), clz, matches).getContent();
        List<AppFunc> allAppFunc = appFuncQuery.findAndAllParent(appFunc.stream()
            .map(x -> x.setHit(true)).collect(Collectors.toList()));
        appFuncQuery.setTargetTags(allAppFunc);
        return allAppFunc;
      }
    }.execute();
  }

}
