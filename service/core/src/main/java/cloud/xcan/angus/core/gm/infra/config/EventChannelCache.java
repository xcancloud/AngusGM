package cloud.xcan.angus.core.gm.infra.config;

import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventChannelCache {

  private final Cache<String, List<EventChannel>> eventChannelCache;

  public EventChannelCache() {
    this.eventChannelCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build();
  }

  public void cacheEventTemplate(String tenantAndTemplateId, List<EventChannel> channels) {
    this.eventChannelCache.put(tenantAndTemplateId, channels);
  }

  public List<EventChannel> getEventTemplate(String tenantAndTemplateId) {
    return this.eventChannelCache.getIfPresent(tenantAndTemplateId);
  }

  public void clearEventTemplates(String tenantAndTemplateId) {
    this.eventChannelCache.invalidate(tenantAndTemplateId);
  }

}
