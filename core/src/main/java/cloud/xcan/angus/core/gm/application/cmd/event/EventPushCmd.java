package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public interface EventPushCmd {

  void sendByJob(EventPush eventPush);

  void add0(ArrayList<EventPush> eventPushes);

  void update0(List<EventPush> emails);

  void updateEventPushFail(Collection<Long> eventIds, String failMsg);
}
