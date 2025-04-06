package cloud.xcan.angus.core.gm.application.query.event.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.event.EventSearch;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.EventSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class EventSearchImpl implements EventSearch {

  @Resource
  private EventSearchRepo eventSearchRepo;

  @Override
  public Page<Event> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Event> clz, String... matches) {
    return new BizTemplate<Page<Event>>() {

      @Override
      protected Page<Event> process() {
        return eventSearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }

}
