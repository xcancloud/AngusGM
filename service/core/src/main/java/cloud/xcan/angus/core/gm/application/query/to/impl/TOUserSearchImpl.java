package cloud.xcan.angus.core.gm.application.query.to.impl;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.to.TOUserSearch;
import cloud.xcan.angus.core.gm.domain.to.TOUserSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class TOUserSearchImpl implements TOUserSearch {

  @Resource
  private TOUserSearchRepo toUserSearchRepo;

  @Override
  public Page<TOUser> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<TOUser> topUserClass, String... matches) {
    return new BizTemplate<Page<TOUser>>() {

      @Override
      protected Page<TOUser> process() {
        return toUserSearchRepo.find(criteria, pageable, topUserClass, matches);
      }
    }.execute();
  }
}
