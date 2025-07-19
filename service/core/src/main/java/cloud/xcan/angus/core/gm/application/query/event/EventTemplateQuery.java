package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface EventTemplateQuery {

  EventTemplate detail(Long id, boolean joinTenantSetting);

  Page<EventTemplate> list(GenericSpecification<EventTemplate> spec,
      boolean joinTenantSetting, PageRequest pageable, boolean fullTextSearch, String[] match);

  EventTemplate checkAndFind(Long id);

  EventTemplate findByEventCode(String eventCode);

  void checkEventNameExist(EventTemplate template);

  void checkEventCodeExist(EventTemplate template);

  void setReceiveSetting(Page<EventTemplate> templates);

}
