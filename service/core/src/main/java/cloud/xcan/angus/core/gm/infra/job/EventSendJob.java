package cloud.xcan.angus.core.gm.infra.job;


import static cloud.xcan.angus.api.commonlink.EventConstant.MAX_EVENT_RESULT_MSG_LENGTH;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;

import cloud.xcan.angus.core.gm.application.cmd.event.EventPushCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventPushQuery;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EventSendJob {

  private static final String LOCK_KEY = "gm:job:EventSendJob";
  private final static int COUNT = 200;
  private final static int MAX_RETRY_NUM = 2;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private EventPushCmd eventPushCmd;

  @Resource
  private EventPushQuery eventPushQuery;

  @Scheduled(fixedDelay = 11 * 1000, initialDelay = 1500)
  public void sendEvent() {
    jobTemplate.execute(LOCK_KEY, 30, TimeUnit.MINUTES, () -> {
      List<EventPush> eventInPending = null;
      try {
        eventInPending = eventPushQuery.findPushEventInPending(COUNT, MAX_RETRY_NUM);
        while (isNotEmpty(eventInPending)) {
          for (EventPush eventPush : eventInPending) {
            eventPushCmd.sendByJob(eventPush);
          }
          eventInPending = eventInPending.size() >= COUNT
              ? eventPushQuery.findPushEventInPending(COUNT, MAX_RETRY_NUM) : null;
        }
      } catch (Exception e) {
        log.error("EventSendJob#Execute fail:", e);
        if (isNotEmpty(eventInPending)) {
          updatePushFailStatus(eventInPending, e);
          updateEventFailStatus(eventInPending, e);
        }
      }
    });
  }

  private void updatePushFailStatus(List<EventPush> eventInPending, Exception e) {
    try {
      eventPushCmd.update0(eventInPending.stream().peek(
              s -> s.setPush(false)
                  .setPushMsg(lengthSafe(e.getMessage(), MAX_EVENT_RESULT_MSG_LENGTH))
                  .setRetryTimes(s.getRetryTimes() + 1))
          .collect(Collectors.toList()));
    } catch (Exception e1) {
      log.error("EventSendJob:sendEvent execute fail, update push status exception:", e1);
    }
  }

  private void updateEventFailStatus(List<EventPush> eventInPending, Exception e) {
    try {
      eventPushCmd.updateEventPushFail(eventInPending.stream()
              .map(EventPush::getEventId).collect(Collectors.toList()),
          lengthSafe(e.getMessage(), MAX_EVENT_RESULT_MSG_LENGTH));
    } catch (Exception e2) {
      log.error("EventSendJob:sendEvent execute fail, update event push status exception:", e2);
    }
  }
}
