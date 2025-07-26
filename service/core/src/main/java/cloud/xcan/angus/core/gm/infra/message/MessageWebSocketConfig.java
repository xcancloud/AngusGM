package cloud.xcan.angus.core.gm.infra.message;

import cloud.xcan.angus.core.gm.application.cmd.message.impl.MessageCenterOnlineCmdImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class MessageWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Bean
  public MessageNoticeService messageCenterNoticeService() {
    return new MessageNoticeService();
  }

  @Bean
  public MessageConnectionListener messageCenterConnectionListener() {
    return new MessageConnectionListener();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Enable memory message proxy
    registry.enableSimpleBroker("/topic", "/queue");

    // Define the routing prefix for messages sent from the client to the server, which will be handled by the server's @MessageMapping methods.
    // For example, the client sends a message to /app/chat → handled by the server's @MessageMapping("/chat").
    registry.setApplicationDestinationPrefixes("/app");

    // Define the routing prefix for private user messages, used for the server to send messages to specific users.
    // The client subscribes to /user/queue/notifications → the server can send messages using convertAndSendToUser(username, "/queue/notifications", msg)
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Configure WebSocket endpoint and add token interceptor
    registry.addEndpoint("/ws/messageCenter")
        .addInterceptors(new AuthHandshakeInterceptor())
        .setAllowedOriginPatterns("*")
        .withSockJS()
        .setHeartbeatTime(15000);
  }

  public static void messageCenterShutdownHook(ConfigurableApplicationContext cac) {
    Thread shutdownThread = new Thread(() -> {
      try {
        MessageCenterOnlineCmdImpl onlineCmd = cac.getBean(MessageCenterOnlineCmdImpl.class);
        onlineCmd.shutdown();
        log.info("Shutdown hook: update current online user to offline");
      } catch (Exception e) {
        log.error("Error during shutdown hook execution", e);
      }
    }, "MessageCenter-Shutdown-Hook");

    Runtime.getRuntime().addShutdownHook(shutdownThread);
  }
}
