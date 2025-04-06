package cloud.xcan.angus.core.gm.infra.job;


import static cloud.xcan.angus.api.commonlink.EventConstant.MAX_EVENT_RESULT_MSG_LENGTH;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;

import cloud.xcan.angus.core.gm.application.cmd.event.EventCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventQuery;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PushEventGenJob {

  private static final String LOCK_KEY = "gm:job:PushEventGenJob";
  private final static int COUNT = 200;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private EventCmd eventCmd;

  @Resource
  private EventQuery eventQuery;

  @Scheduled(fixedDelay = 15 * 1000, initialDelay = 1700)
  public void genPushEvent() {
    jobTemplate.execute(LOCK_KEY, 20, TimeUnit.MINUTES, () -> {
      List<Event> eventInPending = null;
      try {
        eventInPending = eventQuery.findEventInUnPush(COUNT);
        while (isNotEmpty(eventInPending)) {
          eventCmd.genPushEvent(eventInPending);
          eventInPending = eventInPending.size() >= COUNT
              ? eventQuery.findEventInUnPush(COUNT) : null;
        }
      } catch (Exception e) {
        log.error("PushEventGenJob#Execute fail:", e);
        if (isNotEmpty(eventInPending)) {
          for (Event event : eventInPending) {
            event.setPushMsg(lengthSafe(e.getMessage(), MAX_EVENT_RESULT_MSG_LENGTH))
                .setPushStatus(EventPushStatus.PUSH_FAIL);
          }
          eventCmd.update0(eventInPending);
        }
      }
    });
  }

}
