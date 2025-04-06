package cloud.xcan.angus.core.gm.infra.config;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmd;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import java.util.HashMap;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

  @Bean("pushServiceMap")
  public HashMap<ReceiveChannelType, EventChannelPushCmd> getPushServiceMap(
      List<EventChannelPushCmd> eventChannelPushCmd) {
    HashMap<ReceiveChannelType, EventChannelPushCmd>
        pushServiceMap = new HashMap<>(eventChannelPushCmd.size());
    for (EventChannelPushCmd eventPushCmd : eventChannelPushCmd) {
      pushServiceMap.put(eventPushCmd.getPkey(), eventPushCmd);
    }
    return pushServiceMap;
  }

  @Bean
  public EventTemplateCache getEventTemplateCache() {
    return new EventTemplateCache();
  }

  @Bean
  public EventChannelCache getEventChannelCache() {
    return new EventChannelCache();
  }

}
