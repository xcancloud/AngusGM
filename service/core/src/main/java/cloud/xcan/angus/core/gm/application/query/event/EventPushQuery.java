package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import java.util.List;

public interface EventPushQuery {

  List<EventPush> findPushEventInPending(int size, int maxRetryNum);
}
