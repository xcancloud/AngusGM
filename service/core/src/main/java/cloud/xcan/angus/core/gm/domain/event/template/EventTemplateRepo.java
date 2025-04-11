package cloud.xcan.angus.core.gm.domain.event.template;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventTemplateRepo extends BaseRepository<EventTemplate, Long> {

  EventTemplate getByEventCode(String code);

  boolean existsByEventCode(String code);

  boolean existsByEventName(String eventName);

  boolean existsByEventNameAndIdNot(String eventName, Long id);

  boolean existsByEventCodeAndIdNot(String code, Long id);

}
