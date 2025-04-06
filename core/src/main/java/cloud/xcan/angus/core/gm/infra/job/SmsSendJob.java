package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.job.JobTemplate;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsSendJob {

  private static final String LOCK_KEY = "gm:job:SmsSendJob";

  private final static int COUNT = 100;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private SmsQuery smsQuery;

  @Resource
  private SmsCmd smsCmd;

  @DoInFuture("Support resending after failure")
  @Scheduled(fixedDelay = 33 * 1000, initialDelay = 1000)
  public void sendEmail() {
    jobTemplate.execute(LOCK_KEY, 20, TimeUnit.MINUTES, () -> {
      List<Sms> smsInPending = null;
      try {
        SmsChannel enabledChannel = smsQuery.checkChannelEnabledAndGet();
        SmsProvider smsProvider = smsQuery.checkAndGetSmsProvider(enabledChannel);

        smsInPending = smsQuery.findSmsInPending(COUNT);
        while (isNotEmpty(smsInPending)) {
          for (Sms sms : smsInPending) {
            smsCmd.sendByJob(sms, enabledChannel, smsProvider);
          }
          smsInPending = smsInPending.size() >= COUNT ? smsQuery.findSmsInPending(COUNT) : null;
        }
      } catch (Exception e) {
        log.error("SmsSendJob#Execute fail:", e);
        if (isNotEmpty(smsInPending)) {
          smsCmd.update0(smsInPending.stream().peek(s -> s.setSendStatus(ProcessStatus.FAILURE))
              .collect(Collectors.toList()));
        }
      }
    });
  }
}
