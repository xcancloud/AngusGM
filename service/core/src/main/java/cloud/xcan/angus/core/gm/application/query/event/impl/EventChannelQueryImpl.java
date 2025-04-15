package cloud.xcan.angus.core.gm.application.query.event.impl;

import static cloud.xcan.angus.core.biz.exception.QuotaException.M.QUOTA_OVER_LIMIT_T2;
import static cloud.xcan.angus.core.gm.application.converter.EventTemplateConverter.getEventChannelCacheKey;
import static cloud.xcan.angus.core.gm.domain.EventCoreMessage.RECEIVE_SETTING_DELETE_IN_USE_KEY;
import static cloud.xcan.angus.core.gm.domain.EventCoreMessage.RECEIVE_SETTING_DELETE_IN_USE_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannelRepo;
import cloud.xcan.angus.core.gm.domain.event.template.EventTemplateRepo;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannel;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannelRepo;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiverRepo;
import cloud.xcan.angus.core.gm.infra.config.EventChannelCache;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EventChannelQueryImpl implements EventChannelQuery {

  @Resource
  private EventChannelRepo eventChannelRepo;

  @Resource
  private EventTemplateReceiverRepo eventTemplateReceiverRepo;

  @Resource
  private EventTemplateRepo eventTemplateRepo;

  @Resource
  private EventTemplateChannelRepo eventTemplateChannelRepo;

  @Resource
  private EventChannelCache eventChannelCache;

  @Override
  public List<EventChannel> channelList(ReceiveChannelType channelType) {
    return new BizTemplate<List<EventChannel>>() {

      @Override
      protected List<EventChannel> process() {
        return eventChannelRepo.findAllByType(channelType);
      }
    }.execute();
  }

  @Override
  public List<ReceiveChannelType> list() {
    return new BizTemplate<List<ReceiveChannelType>>() {
      @Override
      protected List<ReceiveChannelType> process() {
        return eventChannelRepo.findAll().stream().map(EventChannel::getType)
            .collect(Collectors.toList());
      }
    }.execute();
  }

  @Override
  public List<EventChannel> findByTemplateId(Long tenantId, Long templateId) {
    return new BizTemplate<List<EventChannel>>() {

      @Override
      protected List<EventChannel> process() {
        String cacheKey = getEventChannelCacheKey(tenantId, templateId);
        List<EventChannel> channels = eventChannelCache.getEventTemplate(cacheKey);
        if (isNotEmpty(channels)) {
          return channels;
        }
        channels = eventChannelRepo.findByTemplateId(tenantId, templateId);
        if (isNotEmpty(channels)) {
          eventChannelCache.cacheEventTemplate(cacheKey, channels);
          return channels;
        }
        return null;
      }
    }.execute();
  }

  @Override
  public EventTemplateReceiver findExecByTemplateId(Long tenantId, Long templateId) {
    return new BizTemplate<EventTemplateReceiver>() {
      @Override
      protected EventTemplateReceiver process() {
        return eventTemplateReceiverRepo.findByTenantIdAndTemplateId(tenantId, templateId);
      }
    }.execute();
  }

  @Override
  public EventChannel checkAndFind(Long id) {
    return eventChannelRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "EventChannel"));
  }

  @Override
  public List<EventChannel> find(Collection<Long> channelIds) {
    List<EventChannel> channels = eventChannelRepo.findAllById(new HashSet<>(channelIds));
    if (channelIds.size() == channels.size()) {
      return channels;
    }
    List<Long> existedIds = channels.stream().map(EventChannel::getId).toList();
    channelIds.removeAll(existedIds);
    throw ResourceNotFound.of(channelIds.iterator().next(), "EventChannel");
  }

  @Override
  public void checkNameExisted(EventChannel channel) {
    Long count = isNull(channel.getId()) ? eventChannelRepo.countByName(channel.getName())
        : eventChannelRepo.countByNameAndIdNot(channel.getName(), channel.getId());
    if (count > 0) {
      throw ResourceExisted.of(channel.getName(), "EventChannel");
    }
  }

  @Override
  public void checkQuota(ReceiveChannelType channelType, int incr) {
    Long count = eventChannelRepo.countByType(channelType);
    if (count + incr > channelType.getQuota()) {
      throw QuotaException.of(QUOTA_OVER_LIMIT_T2,
          new Object[]{String.format("EventChannel[%s]", channelType.getValue()),
              channelType.getQuota()});
    }
  }

  @Override
  public void checkNotInUse(Long id) {
    List<EventTemplateChannel> templateChannels = eventTemplateChannelRepo.findAllByChannelId(id);
    if (isNotEmpty(templateChannels)) {
      List<Long> templateIds = templateChannels.stream()
          .map(EventTemplateChannel::getTemplateId).collect(Collectors.toList());
      List<EventTemplate> eventTemplates = eventTemplateRepo.findAllById(templateIds);
      if (isNotEmpty(eventTemplates)) {
        throw BizException.of(RECEIVE_SETTING_DELETE_IN_USE_KEY,
            RECEIVE_SETTING_DELETE_IN_USE_T, new Object[]{eventTemplates.get(0).getEventName()});
      }
    }
  }
}
