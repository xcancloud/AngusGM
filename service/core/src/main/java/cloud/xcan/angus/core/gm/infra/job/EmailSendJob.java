package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSendJob {

  private static final String TENANT_EMAIL_LOCK_KEY = "gm:job:EmailSendJob:sendTenantScopeEmail";
  private static final String PLATFORM_EMAIL_LOCK_KEY = "gm:job:EmailSendJob:sendPlatformScopeEmail";

  private final static int COUNT = 100;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private EmailQuery emailQuery;

  @Resource
  private EmailCmd emailCmd;

  @Scheduled(fixedDelay = 10 * 1000, initialDelay = 1000)
  public void sendTenantScopeEmail() {
    jobTemplate.execute(TENANT_EMAIL_LOCK_KEY, 60, TimeUnit.MINUTES, () -> {
      List<Email> emailInPending = null;
      try {
        emailInPending = emailQuery.findTenantEmailInPending(COUNT);
        while (isNotEmpty(emailInPending)) {
          for (Email email : emailInPending) {
            emailCmd.sendByJob(email);
          }
          emailInPending = emailInPending.size() >= COUNT
              ? emailQuery.findTenantEmailInPending(COUNT) : null;
        }
      } catch (Exception e) {
        log.error("SendTenantScopeEmail execute fail: {}", e.getMessage());
        if (isNotEmpty(emailInPending)) {
          emailCmd.update0(emailInPending.stream().peek(s -> s.setSendStatus(ProcessStatus.FAILURE))
              .collect(Collectors.toList()));
        }
      }
    });
  }

  @Scheduled(fixedDelay = 15 * 1000, initialDelay = 5000)
  public void sendPlatformScopeEmail() {
    jobTemplate.execute(PLATFORM_EMAIL_LOCK_KEY, 120, TimeUnit.MINUTES, () -> {
      List<Email> emailInPending = null;
      try {
        emailInPending = emailQuery.findPlatformEmailInPending(COUNT);
        while (isNotEmpty(emailInPending)) {
          for (Email email : emailInPending) {
            emailCmd.sendByJob(email);
          }
          emailInPending = emailInPending.size() >= COUNT
              ? emailQuery.findTenantEmailInPending(COUNT) : null;
        }
      } catch (Exception e) {
        log.error("SendPlatformScopeEmail#Execute fail:", e);
        if (isNotEmpty(emailInPending)) {
          emailCmd.update0(emailInPending.stream().peek(s -> s.setSendStatus(ProcessStatus.FAILURE))
              .collect(Collectors.toList()));
        }
      }
    });
  }
}
