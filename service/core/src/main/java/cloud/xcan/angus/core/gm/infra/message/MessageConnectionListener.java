package cloud.xcan.angus.core.gm.infra.message;

import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_PRINCIPAL;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR;

import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listen to all WebSocket events.
 */
@Slf4j
public class MessageConnectionListener implements
    ApplicationListener<AbstractSubProtocolEvent> {

  public static final Map<String, String> LOCAL_ONLINE_USERS = new ConcurrentHashMap<>();

  @Resource
  private MessageCenterOnlineCmd messageCenterOnlineCmd;

  @Override
  public void onApplicationEvent(AbstractSubProtocolEvent event) {
    if (event instanceof SessionConnectEvent event0) {
      // Connection established event
      handleConnection(event0);
    } else if (event instanceof SessionDisconnectEvent event0) {
      // Connection disconnected event
      handleDisconnection(event0);
    }
  }

  private void handleConnection(SessionConnectEvent event) {
    String sessionId = Objects.requireNonNull(
        event.getMessage().getHeaders().get("simpSessionId")).toString();
    BearerTokenAuthentication user = (BearerTokenAuthentication) event.getUser();
    Map<String, Object> attributes = user.getTokenAttributes();
    Map<String, Object> principal = (Map<String, Object>) attributes.get(INTROSPECTION_CLAIM_NAMES_PRINCIPAL);
    String userAgent = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT).toString();
    String deviceId = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID).toString();
    String remoteAddress = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR).toString();

    String username = event.getUser().getName();
    LOCAL_ONLINE_USERS.put(sessionId, username);
    messageCenterOnlineCmd.updateOnlineStatus(username, userAgent, deviceId, remoteAddress, true);
    log.info("MessageCenter: {} connected，Session ID: {}", username, sessionId);
  }

  private void handleDisconnection(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    BearerTokenAuthentication user = (BearerTokenAuthentication) event.getUser();
    Map<String, Object> attributes = user.getTokenAttributes();
    Map<String, Object> principal = (Map<String, Object>) attributes.get(INTROSPECTION_CLAIM_NAMES_PRINCIPAL);
    String userAgent = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT).toString();
    String deviceId = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID).toString();
    String remoteAddress = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR).toString();

    String username = LOCAL_ONLINE_USERS.remove(sessionId);
    messageCenterOnlineCmd.updateOnlineStatus(username, userAgent, deviceId, remoteAddress, false);
    log.info("MessageCenter: {} disconnected，Session ID: {}", username, sessionId);
  }

}
