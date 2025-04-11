package cloud.xcan.angus.core.gm.application.query.tag.impl;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagSearch;
import cloud.xcan.angus.core.gm.domain.tag.WebTagSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class WebTagSearchImpl implements WebTagSearch {

  @Resource
  private WebTagSearchRepo webTagSearchRepo;

  @Override
  public Page<WebTag> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<WebTag> clz, String... matches) {
    return new BizTemplate<Page<WebTag>>() {

      @Override
      protected Page<WebTag> process() {
        return webTagSearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }

}
