package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface EventQuery {

  Event detail(Long id);

  Page<Event> list(GenericSpecification<Event> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Event checkAndFind(Long id);

  List<Event> findEventInUnPush(int size);

}
