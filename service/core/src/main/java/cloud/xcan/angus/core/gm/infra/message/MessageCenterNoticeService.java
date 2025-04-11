package cloud.xcan.angus.core.gm.infra.message;

import static cloud.xcan.angus.core.gm.infra.message.MessageCenterConnectionListener.LOCAL_ONLINE_USERS;

import jakarta.annotation.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
  public void sendUserMessage(String username, String message) {
    if (LOCAL_ONLINE_USERS.containsValue(username)) {
      messagingTemplate.convertAndSendToUser(username, "/queue/message", message);
    } else {
      // TODO Use Redis broadcasting for sending messages during multi-instance deployment.
    }
  }

}
