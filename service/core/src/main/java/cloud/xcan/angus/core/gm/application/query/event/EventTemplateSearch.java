package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventTemplateSearch {

  Page<EventTemplate> search(Set<SearchCriteria> criteria, boolean joinTenantSetting,
      Pageable pageable, Class<EventTemplate> clz, String... matches);

}
