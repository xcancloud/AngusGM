package cloud.xcan.angus.core.gm.infra.message;

import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_USERNAME;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.PRINCIPAL;

import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listen to all WebSocket events.
 */
@Slf4j
public class MessageConnectionListener implements ApplicationListener<AbstractSubProtocolEvent> {

  private static final Map<String, String> LOCAL_ONLINE_USERS_INTERNAL = new ConcurrentHashMap<>();
  public static final Map<String, String> LOCAL_ONLINE_USERS = Collections.unmodifiableMap(
      LOCAL_ONLINE_USERS_INTERNAL);

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
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
    Map<String, Object> principal = (Map<String, Object>) sessionAttributes.get(PRINCIPAL);

    if (principal == null) {
      log.warn("Principal is null for session: {}", sessionId);
      return;
    }

    String username = Optional.ofNullable(principal.get(INTROSPECTION_CLAIM_NAMES_USERNAME))
        .map(Object::toString).orElse("unknown");
    String userAgent = Optional.ofNullable(principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT))
        .map(Object::toString).orElse("unknown");
    String deviceId = Optional.ofNullable(
            principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID))
        .map(Object::toString).orElse("unknown");
    String remoteAddress = Optional.ofNullable(
            principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR))
        .map(Object::toString).orElse("unknown");

    LOCAL_ONLINE_USERS_INTERNAL.put(sessionId, username);
    messageCenterOnlineCmd.updateOnlineStatus(sessionId, username, userAgent, deviceId,
        remoteAddress, true);
    log.info("MessageCenter: {} connected，Session ID: {}", username, sessionId);
  }

  private void handleDisconnection(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    String username = LOCAL_ONLINE_USERS_INTERNAL.remove(sessionId);
    if (username != null) {
      messageCenterOnlineCmd.updateOnlineStatus(sessionId, username, null, null, null, false);
      log.info("MessageCenter: {} disconnected，Session ID: {}", username, sessionId);
    }
  }

}
