package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface EventCmd {

  List<IdKey<Long, Object>> add(List<Event> events);

  void genPushEvent(List<Event> events);

  void update0(List<Event> collect);
}
