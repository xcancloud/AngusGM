package cloud.xcan.angus.config;

import cloud.xcan.angus.web.SneakyLogConfigurer;
import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  @ConditionalOnMissingBean
  public SneakyLogConfigurer sneakyLogConfigurer() {
    return new SneakyLogConfigurer();
  }

  @Bean
  @ConditionalOnMissingBean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

}
