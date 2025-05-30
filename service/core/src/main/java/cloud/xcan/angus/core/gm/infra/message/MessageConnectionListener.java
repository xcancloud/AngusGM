package cloud.xcan.angus.core.gm.infra.message;

import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR;
import static cloud.xcan.angus.security.model.SecurityConstant.INTROSPECTION_CLAIM_NAMES_USERNAME;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.PRINCIPAL;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Objects;
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
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
    Map<String, Object> principal = (Map<String, Object>) sessionAttributes.get(PRINCIPAL);
    String username = principal.get(INTROSPECTION_CLAIM_NAMES_USERNAME).toString();
    String userAgent = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT).toString();
    String deviceId = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID).toString();
    String remoteAddress = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR).toString();

    LOCAL_ONLINE_USERS.put(sessionId, username);
    messageCenterOnlineCmd.updateOnlineStatus(username, userAgent, deviceId, remoteAddress, true);
    log.info("MessageCenter: {} connected，Session ID: {}", username, sessionId);
  }

  private void handleDisconnection(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
    Map<String, Object> principal = (Map<String, Object>) sessionAttributes.get(PRINCIPAL);
    Object userAgent = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_AGENT);
    Object deviceId = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_DEVICE_ID);
    Object remoteAddress = principal.get(INTROSPECTION_CLAIM_NAMES_REQUEST_REMOTE_ADDR);

    String username = LOCAL_ONLINE_USERS.remove(sessionId);
    messageCenterOnlineCmd.updateOnlineStatus(username, stringSafe(userAgent), stringSafe(deviceId),
        stringSafe(remoteAddress), false);
    log.info("MessageCenter: {} disconnected，Session ID: {}", username, sessionId);
  }

}
