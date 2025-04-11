package cloud.xcan.angus.core.gm.application.query.group.impl;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupSearch;
import cloud.xcan.angus.core.gm.domain.group.GroupSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class GroupSearchImpl implements GroupSearch {

  @Resource
  private GroupSearchRepo groupSearchRepo;

  @Resource
  private GroupQuery groupQuery;

  @Override
  public Page<Group> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Group> clz, String... matches) {
    return new BizTemplate<Page<Group>>(true, true) {

      @Override
      protected Page<Group> process() {
        Page<Group> groupPage = groupSearchRepo.find(criteria, pageable, clz, matches);
        if (groupPage.hasContent()) {
          groupQuery.setUserNum(groupPage.getContent());
        }
        return groupPage;
      }
    }.execute();
  }
}
