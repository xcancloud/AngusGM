package cloud.xcan.angus;

import static cloud.xcan.angus.core.gm.infra.message.MessageWebSocketConfig.messageCenterShutdownHook;
import static org.springframework.boot.SpringApplication.run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients(basePackages = {
    "cloud.xcan.angus.api",
    "cloud.xcan.angus.security",
    "cloud.xcan.angus.core.gm.infra.remote"
})
@EnableEurekaServer
@SpringBootApplication(scanBasePackages = {"cloud.xcan.angus"})
public class XCanAngusGMApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = run(XCanAngusGMApplication.class, args);

    messageCenterShutdownHook(context);
  }

}
