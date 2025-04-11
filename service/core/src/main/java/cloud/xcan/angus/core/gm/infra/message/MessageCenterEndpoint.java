package cloud.xcan.angus.core.gm.infra.message;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MessageCenterEndpoint {

  /**
   * Receive messages sent from the client to the server.
   */
  @MessageMapping("/message")
  public String greeting(String message) throws Exception {
    return "Do not use WebSocket to send messages to the server. Please use RESTFul api for sending instead.";
  }

}
