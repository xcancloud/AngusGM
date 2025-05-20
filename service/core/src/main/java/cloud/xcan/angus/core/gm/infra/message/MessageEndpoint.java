package cloud.xcan.angus.core.gm.infra.message;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.enums.MessageType;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.manager.UserManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Tag(name = "MessageCenterWS", description = "Message center WebSocket sending and receiving api.")
@Controller
public class MessageEndpoint {

  @Resource
  private UserManager userManager;

  /**
   * Receive messages sent from the client to the server.
   */
  @MessageMapping("/chat.send")
  public String greeting(Message message, Principal principal) {
    //Broadcast original message (optional)
    // message.setFrom(principal.getName())
    //    .setType(MessageType.CHAT)
    //    .setMediaType(PushMediaType.PLAIN_TEXT);
    // messagingTemplate.convertAndSend("/topic/public", message);

  //    // 构建自动回复
  //    UserBase userBase = userManager.findUserBase(principal.getName());
  //    Message reply = Message;
  //    reply.set("System");
  //    reply.setContent("已收到您的消息: " + message.getContent());
  //    reply.setType(ChatMessage.MessageType.REPLY);
  //
  //    // 发送给指定用户
  //    messagingTemplate.convertAndSendToUser(
  //        principal.getName(),
  //        "/queue/replies",
  //        reply
  //    );

    return "Do not use WebSocket to send messages to the server. Please use RESTFul api for sending instead.";
  }

}
