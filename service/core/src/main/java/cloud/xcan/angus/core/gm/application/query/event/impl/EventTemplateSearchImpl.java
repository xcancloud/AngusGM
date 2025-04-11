package cloud.xcan.angus.core.gm.application.query.event.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateSearch;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.template.EventTemplateSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class EventTemplateSearchImpl implements EventTemplateSearch {

  @Resource
  private EventTemplateSearchRepo eventTemplateSearchRepo;

  @Resource
  private EventTemplateQuery eventTemplateQuery;

  @Override
  public Page<EventTemplate> search(Set<SearchCriteria> criteria, boolean joinTenantSetting,
      Pageable pageable, Class<EventTemplate> clz, String... matches) {
    return new BizTemplate<Page<EventTemplate>>() {

      @Override
      protected Page<EventTemplate> process() {
        Page<EventTemplate> page = eventTemplateSearchRepo
            .find(criteria, pageable, clz, matches);
        if (page.hasContent() && joinTenantSetting) {
          eventTemplateQuery.setReceiveSetting(page);
        }
        return page;
      }
    }.execute();
  }
}
