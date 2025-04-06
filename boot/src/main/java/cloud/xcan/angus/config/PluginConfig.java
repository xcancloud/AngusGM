package cloud.xcan.angus.config;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.plugin.core.PluginStateListener;
import cloud.xcan.angus.plugin.spring.SpringPluginManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PluginConfig {

  @Bean
  @ConditionalOnMissingBean
  public SpringPluginManager pluginManager(
      @Autowired(required = false) List<PluginStateListener> listeners) {
    SpringPluginManager springPluginManager = new SpringPluginManager();
    if (isNotEmpty(listeners)) {
      for (PluginStateListener pluginStateListener : listeners) {
        springPluginManager.addPluginStateListener(pluginStateListener);
      }
    }
    return springPluginManager;
  }

}
