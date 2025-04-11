package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.spec.annotations.CloudServiceEdition;
import jakarta.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@CloudServiceEdition
@Conditional(CloudServiceEditionCondition.class)
@Configuration
public class TenantUnlockedJob {

  private static final String LOCK_KEY = "gm:job:TenantUnlockedJob";
  private static final Long COUNT = 200L;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private TenantCmd tenantCmd;

  @Scheduled(fixedDelay = 32 * 1000, initialDelay = 3000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 5, TimeUnit.MINUTES, () -> {
      Set<Long> unlockExpireTenantIds = tenantQuery.findUnlockExpire(COUNT);
      while (isNotEmpty(unlockExpireTenantIds)) {
        tenantCmd.unlockExpire(unlockExpireTenantIds);
        unlockExpireTenantIds = unlockExpireTenantIds.size() >= COUNT ?
            tenantQuery.findUnlockExpire(COUNT) : null;
      }
    });
  }

}
