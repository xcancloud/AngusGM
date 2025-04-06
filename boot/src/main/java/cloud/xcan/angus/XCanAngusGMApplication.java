package cloud.xcan.angus;

import cloud.xcan.angus.core.gm.application.cmd.message.impl.MessageCenterOnlineCmdImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients(basePackages = {"cloud.xcan.angus.api.gm"})
@SpringBootApplication
public class XCanAngusGMApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext cac = SpringApplication.run(XCanAngusGMApplication.class, args);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      MessageCenterOnlineCmdImpl onlineCmd = cac.getBean(MessageCenterOnlineCmdImpl.class);
      onlineCmd.shutdown();
      log.info("Shutdown hook: update current online user to offline");
    }));
  }

}
