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
public class UserLockJob {

  private static final String LOCK_KEY = "gm:job:UserLockJob";

  private static final Long COUNT = 200L;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private UserQuery userQuery;

  @Resource
  private UserCmd userCmd;

  @Scheduled(fixedDelay = 33 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 5, TimeUnit.MINUTES, () -> {
      Set<Long> lockExpireUserIds = userQuery.findLockExpire(COUNT);
      while (isNotEmpty(lockExpireUserIds)) {
        userCmd.lockExpire(lockExpireUserIds);
        lockExpireUserIds = lockExpireUserIds.size() >= COUNT
            ? userQuery.findLockExpire(COUNT) : null;
      }
    });
  }
}
