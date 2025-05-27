package cloud.xcan.angus.core.gm.application.cmd.event.impl;


import static cloud.xcan.angus.api.commonlink.EventConstant.EVENT_DUPLICATE_KEY_EXPIRE;
import static cloud.xcan.angus.api.commonlink.EventConstant.EVENT_DUPLICATE_REDIS_KEY;
import static cloud.xcan.angus.core.gm.application.converter.EventConverter.eventToNoticeMessage;
import static cloud.xcan.angus.core.gm.application.converter.EventConverter.toPushEvent;
import static cloud.xcan.angus.core.gm.domain.EventMessage.EVENT_PUSH_INVALID;
import static cloud.xcan.angus.core.gm.domain.EventMessage.EVENT_PUSH_TEMPLATE_NOT_FOUND;
import static cloud.xcan.angus.core.gm.domain.EventMessage.EVENT_PUSH_TEMPLATE_SETTING_NOT_FOUND_T;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getDefaultLanguage;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.gm.notice.CombinedNoticeDoorRemote;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventPushCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.EventRepo;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.locale.MessageHolder;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Biz
public class EventCmdImpl extends CommCmd<Event, Long> implements EventCmd {

  @Resource
  protected EventRepo eventRepo;

  @Resource
  protected EventPushCmd eventPushCmd;

  @Resource
  protected EventTemplateQuery eventTemplateQuery;

  @Resource
  protected EventChannelQuery eventChannelQuery;

  @Resource
  private CombinedNoticeDoorRemote noticeDoorRemote;

  @Resource
  private RedisService<String> stringRedisService;

  @Value("${xcan.event.gmApiUrlPrefix}")
  private String eventUrlPrefix;

  // @Transactional(rollbackFor = Exception.class) <- Use the inner @Transactional in batchInsert
  @Override
  public List<IdKey<Long, Object>> add(List<Event> events) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        try {
          List<Event> duplicatedEvents = getDuplicatedEvents(events);
          if (isEmpty(duplicatedEvents)) {
            return null;
          }

          List<IdKey<Long, Object>> idKeys = new ArrayList<>();

          // Send notification type event immediately
          List<Event> noticeTypeEvents = duplicatedEvents.stream().filter(
              event -> event.getSourceData().isNoticeType()).collect(Collectors.toList());
          if (isNotEmpty(noticeTypeEvents)) {
            setEventName(noticeTypeEvents);
            sendNoticeTypeMessage(noticeTypeEvents);
            // @Transactional <- Use the inner @Transactional
            idKeys.addAll(batchInsert(noticeTypeEvents));
          }

          // Save non notification type events
          List<Event> nonNoticeTypeEvents = duplicatedEvents.stream().filter(
              event -> !event.getSourceData().isNoticeType()).collect(Collectors.toList());
          if (isNotEmpty(nonNoticeTypeEvents)) {
            for (Event event : nonNoticeTypeEvents) {
              setEventName(nonNoticeTypeEvents);
              event.setPushStatus(EventPushStatus.PENDING);
            }
            // @Transactional <- Use the inner @Transactional
            idKeys.addAll(batchInsert(nonNoticeTypeEvents));
          }

          return idKeys;
        } catch (Exception e) {
          // Important:: Prevent triggering circular exception events in case of exception.
          log.error("Save events exception:", e);
          return null;
        }
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void genPushEvent(List<Event> events) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        Locale locale = getDefaultLanguage().toLocale();

        ArrayList<EventPush> configuredEventPushes = new ArrayList<>();
        for (Event event : events) {
          // Mark event as failure when no template is configured
          EventTemplate eventTemplate = eventTemplateQuery.findByEventCode(event.getCode());
          if (isEmpty(eventTemplate)) {
            event.setPushStatus(EventPushStatus.IGNORED);
            event.setPushMsg(MessageHolder.message(EVENT_PUSH_TEMPLATE_NOT_FOUND, locale));
            continue;
          }

          // Mark event as failure when no channel is configured
          List<EventChannel> eventChannels = eventChannelQuery
              .findByTemplateId(event.getTenantId(), eventTemplate.getId());
          if (isEmpty(eventChannels)) {
            event.setPushStatus(EventPushStatus.IGNORED);
            String message = MessageHolder.message(EVENT_PUSH_TEMPLATE_SETTING_NOT_FOUND_T,
                new Object[]{eventTemplate.getEventName(), eventTemplate.getEventCode(),
                    eventTemplate.getEventType()}, locale);
            event.setPushMsg(message);
            continue;
          }

          // Generate push events
          for (EventChannel channel : eventChannels) {
            configuredEventPushes.add(toPushEvent(event, channel, eventUrlPrefix));
          }
          event.setPushStatus(EventPushStatus.PUSHING);
        }

        // Save tenant events status
        eventRepo.saveAll(events);

        // Save push events
        if (isNotEmpty(configuredEventPushes)) {
          eventPushCmd.add0(configuredEventPushes);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update0(List<Event> events) {
    eventRepo.saveAll(events);
  }

  private List<Event> getDuplicatedEvents(List<Event> events) {
    List<Event> duplicatedEvents = new ArrayList<>();
    for (Event event : events) {
      if (nonNull(event.getDuplicateKey())) {
        Boolean lockRes = stringRedisService.setIfAbsent(
            EVENT_DUPLICATE_REDIS_KEY + event.getDuplicateKey(),
            LocalDateTime.now().format(ISO_LOCAL_DATE_TIME),
            EVENT_DUPLICATE_KEY_EXPIRE, TimeUnit.MINUTES);
        if (lockRes) {
          duplicatedEvents.add(event);
        } else {
          log.info("Delete duplicated event within {} minutes, event: {}",
              EVENT_DUPLICATE_KEY_EXPIRE, event);
        }
      }
    }
    return duplicatedEvents;
  }

  private void setEventName(List<Event> events) {
    for (Event event : events) {
      // Cached template
      EventTemplate template = eventTemplateQuery.findByEventCode(event.getCode());
      event.setName(nonNull(template) ? template.getEventName() : "UNKNOWN");
    }
  }

  public void sendNoticeTypeMessage(List<Event> noticeTypeEvents) {
    for (Event event : noticeTypeEvents) {
      try {
        if (isNotEmpty(event.getSourceData())
            && isNotEmpty(event.getSourceData().getNoticeTypes())) {
          noticeDoorRemote.send(eventToNoticeMessage(event.getSourceData())).orElseThrow();
          event.setPushStatus(EventPushStatus.PUSH_SUCCESS);
        } else {
          event.setPushStatus(EventPushStatus.IGNORED);
          event.setPushMsg(MessageHolder.message(EVENT_PUSH_INVALID));
        }
      } catch (Exception e) {
        log.error("Send notification event exception, cause: {}", e.getMessage());
        event.setPushStatus(EventPushStatus.PUSH_FAIL).setPushMsg(e.getMessage());
      }
    }
  }

  @Override
  protected BaseRepository<Event, Long> getRepository() {
    return eventRepo;
  }
}
