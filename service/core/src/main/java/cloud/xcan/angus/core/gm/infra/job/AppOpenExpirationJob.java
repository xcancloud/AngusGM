package cloud.xcan.angus.core.gm.infra.job;


import cloud.xcan.angus.core.gm.application.cmd.app.AppOpenCmd;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppOpenExpirationJob {

  private static final String LOCK_KEY = "gm:job:AppOpenExpirationJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private AppOpenCmd appOpenCmd;

  @Scheduled(fixedDelay = 59 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 5, TimeUnit.MINUTES, () -> {
      appOpenCmd.expiredUpdate();
    });
  }
}
