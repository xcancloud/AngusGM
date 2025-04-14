package cloud.xcan.angus.core.gm.infra.message;

import static cloud.xcan.angus.core.gm.infra.message.MessageCenterConnectionListener.LOCAL_ONLINE_USERS;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import jakarta.annotation.Resource;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
public class MessageCenterNoticeService {

  @Resource
  private SimpMessagingTemplate messagingTemplate;

  /**
   * Broadcast messages to all clients subscribed to /topic/message.
   */
  public void sendBroadcast(String message) {
    messagingTemplate.convertAndSend("/topic/message", message);
  }

  /**
   * Send private messages to a specified user (the user client must subscribe to
   * /user/queue/message).
   */
  public void sendUserMessage(Collection<String> usernames, String message) {
    if (isNotEmpty(usernames)) {
      for (String username : usernames) {
        if (LOCAL_ONLINE_USERS.containsValue(username)) {
          try {
            messagingTemplate.convertAndSendToUser(username, "/queue/message", message);
            log.info("[WorkOrder] Message sent to user {}} : {}", username, message);
          } catch (Exception e) {
            log.error("Send notice message to user[{}] exception: ", username, e);
          }
        }
      }

    }
  }
}
