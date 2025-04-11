package cloud.xcan.angus.core.gm.application.query.api.impl;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.api.ApiSearch;
import cloud.xcan.angus.core.gm.domain.api.ApiSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class ApiSearchImpl implements ApiSearch {

  @Resource
  private ApiSearchRepo apiSearchRepo;

  @Override
  public Page<Api> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Api> cls,
      String... matches) {
    return new BizTemplate<Page<Api>>() {

      @Override
      protected Page<Api> process() {
        return apiSearchRepo.find(criteria, pageable, cls, matches);
      }
    }.execute();
  }
}
