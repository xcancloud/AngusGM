package cloud.xcan.angus.core.gm.application.query.event.impl;

import static cloud.xcan.angus.core.biz.exception.QuotaException.M.QUOTA_OVER_LIMIT_T2;
import static cloud.xcan.angus.core.gm.application.converter.EventTemplateConverter.getEventChannelCacheKey;
import static cloud.xcan.angus.core.gm.domain.EventMessage.RECEIVE_SETTING_DELETE_IN_USE_KEY;
import static cloud.xcan.angus.core.gm.domain.EventMessage.RECEIVE_SETTING_DELETE_IN_USE_T;
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

/**
 * <p>
 * Implementation of event channel query operations.
 * </p>
 * <p>
 * Manages event channel retrieval, caching, and quota validation.
 * Provides comprehensive event channel querying with template association support.
 * </p>
 * <p>
 * Supports channel listing, template association queries, quota validation,
 * and usage checking for comprehensive event channel management.
 * </p>
 */
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

  /**
   * <p>
   * Retrieves event channels by channel type.
   * </p>
   * <p>
   * Returns all channels of the specified type for event processing.
   * </p>
   */
  @Override
  public List<EventChannel> channelList(ReceiveChannelType channelType) {
    return new BizTemplate<List<EventChannel>>() {

      @Override
      protected List<EventChannel> process() {
        return eventChannelRepo.findAllByType(channelType);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves all supported receive channel types.
   * </p>
   * <p>
   * Returns list of all channel types available in the system.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves event channels associated with template using caching.
   * </p>
   * <p>
   * Uses cache for performance optimization and returns channels for template.
   * Returns null if no channels are associated with the template.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves event template receiver for template execution.
   * </p>
   * <p>
   * Returns receiver configuration for the specified template and tenant.
   * </p>
   */
  @Override
  public EventTemplateReceiver findExecByTemplateId(Long tenantId, Long templateId) {
    return new BizTemplate<EventTemplateReceiver>() {
      @Override
      protected EventTemplateReceiver process() {
        return eventTemplateReceiverRepo.findByTenantIdAndTemplateId(tenantId, templateId);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves event channel by ID.
   * </p>
   * <p>
   * Verifies channel exists and returns channel information.
   * Throws ResourceNotFound exception if channel does not exist.
   * </p>
   */
  @Override
  public EventChannel checkAndFind(Long id) {
    return eventChannelRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "EventChannel"));
  }

  /**
   * <p>
   * Validates and retrieves multiple event channels by IDs.
   * </p>
   * <p>
   * Verifies all channels exist and returns channel information.
   * Throws ResourceNotFound exception if any channel does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Validates channel name uniqueness.
   * </p>
   * <p>
   * Checks if channel name already exists for new or updated channels.
   * Throws ResourceExisted exception if name is not unique.
   * </p>
   */
  @Override
  public void checkNameExisted(EventChannel channel) {
    Long count = isNull(channel.getId()) ? eventChannelRepo.countByName(channel.getName())
        : eventChannelRepo.countByNameAndIdNot(channel.getName(), channel.getId());
    if (count > 0) {
      throw ResourceExisted.of(channel.getName(), "EventChannel");
    }
  }

  /**
   * <p>
   * Validates event channel quota for specified channel type.
   * </p>
   * <p>
   * Checks if adding channels would exceed quota limits for the channel type.
   * Throws appropriate exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkQuota(ReceiveChannelType channelType, int incr) {
    Long count = eventChannelRepo.countByType(channelType);
    if (count + incr > channelType.getQuota()) {
      throw QuotaException.of(QUOTA_OVER_LIMIT_T2,
          new Object[]{String.format("EventChannel[%s]", channelType.getValue()),
              channelType.getQuota()});
    }
  }

  /**
   * <p>
   * Validates channel is not in use by any templates.
   * </p>
   * <p>
   * Checks if channel is associated with any event templates.
   * Throws appropriate exception if channel is currently in use.
   * </p>
   */
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
