package cloud.xcan.angus.core.gm.infra.message;

import static cloud.xcan.angus.core.gm.infra.message.MessageConnectionListener.LOCAL_ONLINE_USERS;
import static cloud.xcan.angus.spec.utils.JsonUtils.toJson;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import jakarta.annotation.Resource;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
public class MessageNoticeService {

  public static final String PUBLIC_TOPIC_DESTINATION = "/topic/messages";
  public static final String PRIVATE_USER_DESTINATION = "/queue/messages";

  @Resource
  private SimpMessagingTemplate messagingTemplate;

  /**
   * Broadcast messages to all clients subscribed to /topic/messages.
   */
  public void sendBroadcast(Message message) {
    messagingTemplate.convertAndSend(PUBLIC_TOPIC_DESTINATION, message);
  }

  /**
   * Send private messages to a specified user (the user client must subscribe to
   * /user/queue/message).
   */
  public void sendUserMessage(Collection<String> usernames, Message message) {
    if (isNotEmpty(usernames)) {
      for (String username : usernames) {
        if (LOCAL_ONLINE_USERS.containsValue(username)) {
          try {
            String jsonMessage = toJson(message);
            assert jsonMessage != null;
            messagingTemplate.convertAndSendToUser(username, PRIVATE_USER_DESTINATION, jsonMessage);
            log.info("Send notice message {}} : {}", username, message);
          } catch (Exception e) {
            log.error("Send notice message to user[{}] exception: ", username, e);
          }
        }
      }
    }
  }
}
