package cloud.xcan.angus.core.gm.domain.event.template;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventTemplateSearchRepo extends CustomBaseRepository<EventTemplate> {

}
