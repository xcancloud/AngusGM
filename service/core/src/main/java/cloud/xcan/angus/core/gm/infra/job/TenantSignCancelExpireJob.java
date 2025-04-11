package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantSignCmd;
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
public class TenantSignCancelExpireJob {

  private static final String LOCK_KEY = "gm:job:TenantSignCancelExpireJob";

  private static final Long COUNT = 200L;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private TenantSignCmd tenantSignCmd;

  @Scheduled(fixedDelay = 30 * 1000, initialDelay = 1000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 5, TimeUnit.MINUTES, () -> {
      Set<Long> cancelRevertTenantIds = tenantQuery.findCancelExpired(COUNT);
      while (isNotEmpty(cancelRevertTenantIds)) {
        tenantSignCmd.signCancelExpire(cancelRevertTenantIds);
        cancelRevertTenantIds = cancelRevertTenantIds.size() >= COUNT
            ? tenantQuery.findCancelExpired(COUNT) : null;
      }
    });
  }

}
