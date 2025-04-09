package cloud.xcan.angus.core.gm.application.converter;

import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannel;
import java.util.Map;

public class EventTemplateConverter {

  public static EventTemplateChannel toEventTemplateChannel(Long id, Long templateId,
      Long channelId, Map<Long, EventChannel> channelMap) {
    return new EventTemplateChannel()
        .setId(id).setTemplateId(templateId)
        .setChannelId(channelId).setChannelType(channelMap.get(channelId).getType());
  }

  public static String getEventChannelCacheKey(Long tenantId, Long templateId) {
    return tenantId + ":" + templateId;
  }

}
