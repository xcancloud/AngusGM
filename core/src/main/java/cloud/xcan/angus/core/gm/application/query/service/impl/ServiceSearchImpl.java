package cloud.xcan.angus.core.gm.application.query.service.impl;

import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceSearch;
import cloud.xcan.angus.core.gm.domain.service.ServiceSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class ServiceSearchImpl implements ServiceSearch {

  @Resource
  private ServiceSearchRepo serviceSearchRepo;

  @Resource
  private ServiceQuery serviceQuery;

  @DoInFuture("Ignore operation services for the tenant client")
  @Override
  public Page<Service> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Service> clz,
      String... matches) {
    return new BizTemplate<Page<Service>>(false) {

      @Override
      protected Page<Service> process() {
        Page<Service> servicePage = serviceSearchRepo.find(criteria, pageable, clz, matches);
        serviceQuery.setApiNum(servicePage.getContent());
        return servicePage;
      }
    }.execute();
  }
}
