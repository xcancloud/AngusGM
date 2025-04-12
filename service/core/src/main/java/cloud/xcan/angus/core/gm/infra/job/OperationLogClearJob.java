package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.register.SettingPropertiesRegister;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.log.OperationLogProperties;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OperationLogClearJob {

  private static final String LOCK_KEY = "gm:job:OperationLogClearJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private SettingPropertiesRegister settingPropertiesRegister;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Scheduled(fixedDelay = 28 * 60 * 1000, initialDelay = 7300)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 60, TimeUnit.MINUTES, () -> {
      OperationLogProperties logProperties = settingPropertiesRegister
          .getRefreshedOperationLogProperties();
      if (settingPropertiesRegister.enabledOperationLog()) {
        operationLogCmd.clearOperationLog(logProperties.getClearBeforeDay());
      }
    });
  }
}
