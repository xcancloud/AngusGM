package cloud.xcan.angus.core.gm.application.cmd.event.impl;


import static cloud.xcan.angus.core.gm.application.converter.EventTemplateConverter.getEventChannelCacheKey;
import static cloud.xcan.angus.core.gm.application.converter.EventTemplateConverter.toEventTemplateChannel;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateChannelCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
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

@Biz
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void channelReplace(Long id, Set<Long> channelIds) {
    new BizTemplate<Void>() {
      EventTemplate templateDb;

      @Override
      protected void checkParams() {
        // Check event template exists
        templateDb = eventTemplateQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        // Delete existed and clear empty template channel.
        eventTemplateChannelRepo.deleteAllByTemplateId(id);

        // Save new template channel.
        if (isNotEmpty(channelIds)) {
          List<EventChannel> channelsDb = eventChannelQuery.find(channelIds);
          Map<Long, EventChannel> channelMap = channelsDb.stream()
              .collect(Collectors.toMap(EventChannel::getId, x -> x));
          List<EventTemplateChannel> eventTemplateChannels = channelIds.stream()
              .map(cid -> toEventTemplateChannel(uidGenerator.getUID(), id, cid, channelMap))
              .collect(Collectors.toList());
          eventTemplateChannelRepo.batchInsert(eventTemplateChannels);

          // Cache event channels.
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
