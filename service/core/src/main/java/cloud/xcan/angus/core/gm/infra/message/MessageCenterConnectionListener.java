package cloud.xcan.angus.core.gm.infra.message;

import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import jakarta.annotation.Resource;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listen to all WebSocket events.
 */
@Slf4j
public class MessageCenterConnectionListener implements
    ApplicationListener<AbstractSubProtocolEvent> {

  public static final Map<String, String> LOCAL_ONLINE_USERS = new ConcurrentHashMap<>();

  @Resource
  private MessageCenterOnlineCmd messageCenterOnlineCmd;

  @Override
  public void onApplicationEvent(AbstractSubProtocolEvent event) {
    // Connection established event
    if (event instanceof SessionConnectEvent) {
      handleConnection((SessionConnectEvent) event);
    }
    // Connection disconnected event
    else if (event instanceof SessionDisconnectEvent) {
      handleDisconnection((SessionDisconnectEvent) event);
    }
  }

  private void handleConnection(SessionConnectEvent event) {
    String username = getCurrentUser(event.getUser());
    String sessionId = Objects.requireNonNull(
        event.getMessage().getHeaders().get("simpSessionId")).toString();
    LOCAL_ONLINE_USERS.put(sessionId, username);
    messageCenterOnlineCmd.updateOnlineStatus(username, true);
    log.info("MessageCenter: {} connected，Session ID: {}", username, sessionId);
  }

  private void handleDisconnection(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    String username = LOCAL_ONLINE_USERS.remove(sessionId);
    messageCenterOnlineCmd.updateOnlineStatus(username, false);
    log.info("MessageCenter: {} disconnected，Session ID: {}", username, sessionId);
  }

  private String getCurrentUser(Principal principal) {
    return (principal != null) ? principal.getName() : "anonymous";
  }
}
