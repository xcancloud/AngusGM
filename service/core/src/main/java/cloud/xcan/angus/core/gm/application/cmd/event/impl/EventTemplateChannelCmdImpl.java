package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.core.gm.application.converter.EventTemplateConverter.getEventChannelCacheKey;
import static cloud.xcan.angus.core.gm.application.converter.EventTemplateConverter.toEventTemplateChannel;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateChannelCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannel;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannelRepo;
import cloud.xcan.angus.core.gm.infra.config.EventChannelCache;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of event template channel command operations for managing template-channel
 * associations.
 *
 * <p>This class provides comprehensive functionality for template-channel management
 * including:</p>
 * <ul>
 *   <li>Managing event template and channel associations</li>
 *   <li>Replacing channel configurations for templates</li>
 *   <li>Caching channel configurations for performance</li>
 *   <li>Handling batch channel assignments</li>
 * </ul>
 *
 * <p>The implementation ensures proper template-channel relationship management
 * with caching for optimal performance.</p>
 */
@org.springframework.stereotype.Service
public class EventTemplateChannelCmdImpl extends CommCmd<EventTemplateChannel, Long>
    implements EventTemplateChannelCmd {

  @Resource
  private EventTemplateChannelRepo eventTemplateChannelRepo;
  @Resource
  private EventTemplateQuery eventTemplateQuery;
  @Resource
  private EventChannelQuery eventChannelQuery;
  @Resource
  private EventChannelCache eventChannelCache;

  /**
   * Replaces channel associations for an event template.
   *
   * <p>This method performs channel replacement including:</p>
   * <ul>
   *   <li>Validating template existence</li>
   *   <li>Removing existing channel associations</li>
   *   <li>Creating new channel associations</li>
   *   <li>Caching channel configurations</li>
   * </ul>
   *
   * @param id         Template identifier
   * @param channelIds Set of channel identifiers to associate
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void channelReplace(Long id, Set<Long> channelIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Validate event template exists
        eventTemplateQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        // Remove existing template-channel associations
        eventTemplateChannelRepo.deleteAllByTemplateId(id);

        // Create new template-channel associations
        if (isNotEmpty(channelIds)) {
          // Retrieve channel configurations
          List<EventChannel> channelsDb = eventChannelQuery.find(channelIds);
          Map<Long, EventChannel> channelMap = channelsDb.stream()
              .collect(Collectors.toMap(EventChannel::getId, x -> x));

          // Create template-channel associations
          List<EventTemplateChannel> eventTemplateChannels = channelIds.stream()
              .map(cid -> toEventTemplateChannel(uidGenerator.getUID(), id, cid, channelMap))
              .collect(Collectors.toList());
          eventTemplateChannelRepo.batchInsert(eventTemplateChannels);

          // Cache event channel configurations for performance
          eventChannelCache.cacheEventTemplate(getEventChannelCacheKey(getTenantId(), id),
              new ArrayList<>(channelMap.values()));
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EventTemplateChannel, Long> getRepository() {
    return eventTemplateChannelRepo;
  }
}
