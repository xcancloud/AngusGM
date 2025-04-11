package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.event.Event;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface EventQuery {

  Event detail(Long id);

  Page<Event> list(Specification<Event> spec, PageRequest pageable);

  Event checkAndFind(Long id);

  List<Event> findEventInUnPush(int size);

}
