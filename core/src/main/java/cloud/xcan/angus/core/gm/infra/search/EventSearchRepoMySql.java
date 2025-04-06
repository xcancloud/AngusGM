package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.EventSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class EventSearchRepoMySql extends SimpleSearchRepository<Event> implements
    EventSearchRepo {

}
