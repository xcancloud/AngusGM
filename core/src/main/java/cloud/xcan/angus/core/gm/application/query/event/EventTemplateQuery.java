package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface EventTemplateQuery {

  EventTemplate detail(Long id, boolean joinTenantSetting);

  Page<EventTemplate> list(Specification<EventTemplate> spec,
      boolean joinTenantSetting, PageRequest pageable);

  EventTemplate checkAndFind(Long id);

  EventTemplate findByEventCode(String eventCode);

  void checkEventNameExist(EventTemplate template);

  void checkEventCodeExist(EventTemplate template);

  void setReceiveSetting(Page<EventTemplate> templates);

}
