package cloud.xcan.angus.core.gm.application.query.event.impl;

import cloud.xcan.angus.core.gm.application.query.event.EventPushQuery;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushRepo;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EventPushQueryImpl implements EventPushQuery {

  @Resource
  private EventPushRepo eventPushRepo;

  @Override
  public List<EventPush> findPushEventInPending(int size, int maxRetryNum) {
    return eventPushRepo.findPushEventInPending(size, maxRetryNum);
  }

}
