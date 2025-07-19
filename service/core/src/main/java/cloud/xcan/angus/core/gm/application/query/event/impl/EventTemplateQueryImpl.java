package cloud.xcan.angus.core.gm.application.query.event.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannelP;
import cloud.xcan.angus.core.gm.domain.event.template.EventTemplateRepo;
import cloud.xcan.angus.core.gm.domain.event.template.EventTemplateSearchRepo;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannelRepo;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiverRepo;
import cloud.xcan.angus.core.gm.infra.config.EventTemplateCache;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EventTemplateQueryImpl implements EventTemplateQuery {

  @Resource
  private EventTemplateRepo eventTemplateRepo;

  @Resource
  private EventTemplateSearchRepo eventTemplateSearchRepo;

  @Resource
  private EventTemplateReceiverRepo eventTemplateReceiverRepo;

  @Resource
  private EventTemplateChannelRepo eventTemplateChannelRepo;

  @Resource
  private EventTemplateCache eventTemplateCache;

  @Override
  public EventTemplate detail(Long id, boolean joinTenantSetting) {
    return new BizTemplate<EventTemplate>() {

      @Override
      protected EventTemplate process() {
        EventTemplate template = eventTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "EventTemplate"));

        if (joinTenantSetting) {
          EventTemplateReceiver receiver = eventTemplateReceiverRepo
              .findByTenantIdAndTemplateId(getOptTenantId(), id);
          template.setReceivers(receiver);

          List<EventChannelP> channels = eventTemplateChannelRepo
              .findChannelByTenantIdAndTemplateId(getOptTenantId(), id);
          template.setChannels(channels);
        }
        return template;
      }
    }.execute();
  }

  @Override
  public Page<EventTemplate> list(GenericSpecification<EventTemplate> spec,
      boolean joinTenantSetting, PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<EventTemplate>>() {

      @Override
      protected Page<EventTemplate> process() {
        Page<EventTemplate> templates = fullTextSearch
            ? eventTemplateSearchRepo.find(spec.getCriteria(), pageable, EventTemplate.class, match)
            : eventTemplateRepo.findAll(spec, pageable);
        if (templates.hasContent() && joinTenantSetting) {
          setReceiveSetting(templates);
        }
        return templates;
      }
    }.execute();
  }

  @Override
  public EventTemplate checkAndFind(Long id) {
    return eventTemplateRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "EventTemplate"));
  }

  @Override
  public EventTemplate findByEventCode(String eventCode) {
    EventTemplate template = eventTemplateCache.getEventTemplate(eventCode);
    if (nonNull(template)) {
      return template;
    }
    template = eventTemplateRepo.getByEventCode(eventCode);
    if (nonNull(template)) {
      eventTemplateCache.cacheEventTemplate(template);
      return template;
    }
    return null;
  }

  @Override
  public void checkEventNameExist(EventTemplate template) {
    boolean existed = isNull(template.getId())
        ? eventTemplateRepo.existsByEventName(template.getEventName())
        : eventTemplateRepo.existsByEventNameAndIdNot(template.getEventName(), template.getId());
    assertResourceExisted(!existed, template.getEventName(), "EventTemplate");
  }

  @Override
  public void checkEventCodeExist(EventTemplate template) {
    boolean existed = isNull(template.getId())
        ? eventTemplateRepo.existsByEventCode(template.getEventCode())
        : eventTemplateRepo.existsByEventCodeAndIdNot(template.getEventCode(), template.getId());
    assertResourceExisted(!existed, template.getEventCode(), "EventTemplate");
  }

  @Override
  public void setReceiveSetting(Page<EventTemplate> templates) {
    Set<Long> templateIds = templates.stream().map(EventTemplate::getId)
        .collect(Collectors.toSet());
    List<EventTemplateReceiver> receivers = eventTemplateReceiverRepo
        .findByTenantIdAndTemplateIdIn(getOptTenantId(), templateIds);
    if (isNotEmpty(receivers)) {
      for (EventTemplate template : templates) {
        for (EventTemplateReceiver receiver : receivers) {
          if (receiver.getTemplateId().equals(template.getId())) {
            template.setReceivers(receiver);
          }
        }
      }
    }

    List<EventChannelP> channels = eventTemplateChannelRepo
        .findChannelByTenantIdAndTemplateIdIn(getOptTenantId(), templateIds);
    if (isNotEmpty(channels)) {
      Map<Long, List<EventChannelP>> templateChannelMap = channels.stream()
          .collect(Collectors.groupingBy(EventChannelP::getTemplateId));
      if (isNotEmpty(templateChannelMap)) {
        for (EventTemplate template : templates) {
          template.setChannels(templateChannelMap.get(template.getId()));
        }
      }
    }
  }
}
