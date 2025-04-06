package cloud.xcan.angus.core.gm.application.query.to.impl;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.to.TORoleSearch;
import cloud.xcan.angus.core.gm.domain.to.TOPolicySearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class TORoleSearchImpl implements TORoleSearch {

  @Resource
  private TOPolicySearchRepo toPolicySearchRepo;

  @Override
  public Page<TORole> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<TORole> clz, String... matches) {
    return new BizTemplate<Page<TORole>>() {

      @Override
      protected Page<TORole> process() {
        return toPolicySearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }
}
