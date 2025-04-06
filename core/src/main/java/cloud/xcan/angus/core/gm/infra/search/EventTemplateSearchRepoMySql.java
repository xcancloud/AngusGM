package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.template.EventTemplateSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class EventTemplateSearchRepoMySql extends SimpleSearchRepository<EventTemplate> implements
    EventTemplateSearchRepo {

}
