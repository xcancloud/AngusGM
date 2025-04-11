package cloud.xcan.angus.core.gm.infra.message;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class MessageCenterWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Bean
  public MessageCenterNoticeService messageCenterNoticeService() {
    return new MessageCenterNoticeService();
  }

  @Bean
  public MessageCenterConnectionListener messageCenterConnectionListener() {
    return new MessageCenterConnectionListener();
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Configure WebSocket endpoint and add token interceptor
    registry.addEndpoint("/ws")
        .setAllowedOrigins("*")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Enable memory message proxy
    registry.enableSimpleBroker("/topic", "/queue");

    // Define the routing prefix for messages sent from the client to the server, which will be handled by the server's @MessageMapping methods.
    // For example, the client sends a message to /app/chat → handled by the server's @MessageMapping("/chat").
    // registry.setApplicationDestinationPrefixes("/app"); <-

    // Define the routing prefix for private user messages, used for the server to send messages to specific users.
    // The client subscribes to /user/queue/notifications → the server can send messages using convertAndSendToUser(username, "/queue/notifications", msg)
    registry.setUserDestinationPrefix("/user");
  }

}
