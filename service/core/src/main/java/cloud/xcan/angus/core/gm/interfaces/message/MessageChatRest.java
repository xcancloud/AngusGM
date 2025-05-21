package cloud.xcan.angus.core.gm.interfaces.message;

import static cloud.xcan.angus.core.gm.infra.message.MessageNoticeService.PRIVATE_USER_DESTINATION;

import cloud.xcan.angus.api.enums.MessageType;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.pojo.Message;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "MessageCenterWS", description = "Message center WebSocket sending and receiving endpoint.")
@RestController
public class MessageChatRest {

  @Resource
  private SimpMessagingTemplate messagingTemplate;

  /**
   * Receive messages sent from the client to the server.
   */
  @MessageMapping("/chat")
  public void handleChatMessage(@Payload Message message, Principal principal,
      @Header("simpSessionId") String sessionId) {
    log.info("Received message from {}, content = {}", principal.getName(), message.toString());

    Message reply = Message.newBuilder()
        .type(MessageType.REPLY)
        .mediaType(PushMediaType.PLAIN_TEXT)
        .content("Do not use WebSocket to send messages to the server. "
            + "Please use RESTFul api for sending instead.")
        .build();

    messagingTemplate.convertAndSendToUser(principal.getName(), PRIVATE_USER_DESTINATION, reply);
  }

}
