package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.domain.message.MessageSentRepo;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class MessageClearJob {

  private static final String LOCK_KEY = "gm:job:MessageClearJob";

  private static final Long USER_RESERVED_NUM = 200L;
  private static final Long COUNT = 2000L;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private MessageSentRepo messageSentRepo;

  /**
   * Only {@link MessageClearJob#USER_RESERVED_NUM} messages are reserved for each user.
   */
  @Transactional(rollbackFor = Exception.class)
  @Scheduled(fixedDelay = 10 * 1000, initialDelay = 3300)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 60, TimeUnit.MINUTES, () -> {
      List<Long> receiveUserIds = messageSentRepo.getReceiveUserIdHavingCount(
          USER_RESERVED_NUM, COUNT);
      if (isNotEmpty(receiveUserIds)) {
        for (Long receiveUserId : receiveUserIds) {
          try {
            messageSentRepo.deleteByReceiveUserIdAndCount(receiveUserId, USER_RESERVED_NUM);
          } catch (Exception e) {
            log.error("MessageClearJob#Execute fail:", e);
          }
        }
      }
    });
  }

}
