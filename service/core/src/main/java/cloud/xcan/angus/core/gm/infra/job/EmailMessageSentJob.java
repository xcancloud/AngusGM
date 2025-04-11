package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.application.cmd.message.MessageCmd;
import cloud.xcan.angus.core.gm.application.query.message.MessageQuery;
import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class EmailMessageSentJob {

  private static final String LOCK_KEY = "gm:job:EmailMessageSentJob";
  private static final int COUNT = 200;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private MessageQuery messageQuery;

  @Resource
  private MessageCmd messageCmd;

  @Scheduled(fixedDelay = 5 * 1000, initialDelay = 3400)
  public void sentEmailMessage() {
    jobTemplate.execute(LOCK_KEY, 10, TimeUnit.MINUTES, () -> {
      List<Message> emailMessages = messageQuery.getPendingMessage(MessageReceiveType.EMAIL, COUNT);
      while (isNotEmpty(emailMessages)) {
        for (Message siteMessage : emailMessages) {
          // Send a message to commit a transaction
          messageCmd.sentEmailMessage(siteMessage);
        }
        emailMessages = emailMessages.size() >= COUNT ?
            messageQuery.getPendingMessage(MessageReceiveType.SITE, COUNT) : null;
      }
    });
  }

}
