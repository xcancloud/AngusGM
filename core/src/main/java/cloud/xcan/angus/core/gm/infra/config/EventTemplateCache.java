package cloud.xcan.angus.core.gm.infra.config;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public class EventTemplateCache {

  private final Cache<String, EventTemplate> eventTemplatesCache;

  public EventTemplateCache() {
    this.eventTemplatesCache = CacheBuilder.newBuilder()
        .maximumSize(64)
        .expireAfterWrite(1, TimeUnit.DAYS)
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build();
  }

  public void cacheEventTemplate(EventTemplate template) {
    this.eventTemplatesCache.put(template.getEventCode(), template);
  }

  public EventTemplate getEventTemplate(String eventCode) {
    return this.eventTemplatesCache.getIfPresent(eventCode);
  }

  public void clearEventTemplates(String eventCode) {
    this.eventTemplatesCache.invalidate(eventCode);
  }

}
