package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserUnlockJob {

  private static final String LOCK_KEY = "gm:job:UserUnlockJob";
  private static final Long COUNT = 200L;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private UserQuery userQuery;

  @Resource
  private UserCmd userCmd;

  @Scheduled(fixedDelay = 34 * 1000, initialDelay = 6000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 5, TimeUnit.MINUTES, () -> {
      Set<Long> unlockExpireUserIds = userQuery.findUnlockExpire(COUNT);
      while (isNotEmpty(unlockExpireUserIds)) {
        userCmd.unlockExpire(unlockExpireUserIds);
        unlockExpireUserIds = unlockExpireUserIds.size() >= COUNT ?
            userQuery.findUnlockExpire(COUNT) : null;
      }
    });
  }
}
