package cloud.xcan.angus.core.gm.application.query.tag.impl;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagSearch;
import cloud.xcan.angus.core.gm.domain.tag.OrgTagSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class OrgTagSearchImpl implements OrgTagSearch {

  @Resource
  private OrgTagSearchRepo orgTagSearchRepo;

  @Override
  public Page<OrgTag> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<OrgTag> clz, String... matches) {
    return new BizTemplate<Page<OrgTag>>(true, true) {

      @Override
      protected Page<OrgTag> process() {
        return orgTagSearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }
}
