package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.core.gm.application.cmd.user.UserDirectoryCmd;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class UserLdapSyncJob {

  private static final String LOCK_KEY = "gm:job:UserLdapSyncJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private UserDirectoryCmd userDirectoryCmd;

  @Scheduled(fixedDelay = 5 * 30 * 1000, initialDelay = 3100)
  public void sync() {
    jobTemplate.execute(LOCK_KEY, 30, TimeUnit.MINUTES, () -> {
      userDirectoryCmd.sync();
    });
  }

}
