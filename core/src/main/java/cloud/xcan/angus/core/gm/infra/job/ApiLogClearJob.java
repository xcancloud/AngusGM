package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.register.SettingPropertiesRegister;
import cloud.xcan.angus.core.gm.application.cmd.api.ApiLogCmd;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.log.ApiLogProperties;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiLogClearJob {

  private static final String LOCK_KEY = "gm:job:ApiLogClearJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private SettingPropertiesRegister settingPropertiesRegister;

  @Resource
  private ApiLogCmd apiLogCmd;

  @Scheduled(fixedDelay = 13 * 60 * 1000, initialDelay = 5300)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 10, TimeUnit.MINUTES, () -> {
      ApiLogProperties properties = settingPropertiesRegister.getRefreshedApiLogProperties();
      if (properties.getEnabled()) {
        apiLogCmd.clearOperationLog(properties.getClearBeforeDay());
      }
    });
  }
}
