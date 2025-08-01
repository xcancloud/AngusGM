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

/**
 * <p>
 * Implementation of event template query operations.
 * </p>
 * <p>
 * Manages event template retrieval, caching, and tenant setting association.
 * Provides comprehensive event template querying with caching support.
 * </p>
 * <p>
 * Supports template detail retrieval, caching, tenant setting association,
 * and name/code uniqueness validation for comprehensive event template management.
 * </p>
 */
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

  /**
   * <p>
   * Retrieves detailed event template information by ID.
   * </p>
   * <p>
   * Fetches complete template record with optional tenant setting association.
   * Throws ResourceNotFound exception if template does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves event templates with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search, specification-based filtering, and tenant setting association.
   * Returns paginated results for comprehensive template management.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves event template by ID.
   * </p>
   * <p>
   * Verifies template exists and returns template information.
   * Throws ResourceNotFound exception if template does not exist.
   * </p>
   */
  @Override
  public EventTemplate checkAndFind(Long id) {
    return eventTemplateRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "EventTemplate"));
  }

  /**
   * <p>
   * Retrieves event template by event code using caching.
   * </p>
   * <p>
   * Uses cache for performance optimization and returns template for event code.
   * Returns null if no template found for the event code.
   * </p>
   */
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

  /**
   * <p>
   * Validates event template name uniqueness.
   * </p>
   * <p>
   * Checks if template name already exists for new or updated templates.
   * Throws ResourceExisted exception if name is not unique.
   * </p>
   */
  @Override
  public void checkEventNameExist(EventTemplate template) {
    boolean existed = isNull(template.getId())
        ? eventTemplateRepo.existsByEventName(template.getEventName())
        : eventTemplateRepo.existsByEventNameAndIdNot(template.getEventName(), template.getId());
    assertResourceExisted(!existed, template.getEventName(), "EventTemplate");
  }

  /**
   * <p>
   * Validates event template code uniqueness.
   * </p>
   * <p>
   * Checks if template code already exists for new or updated templates.
   * Throws ResourceExisted exception if code is not unique.
   * </p>
   */
  @Override
  public void checkEventCodeExist(EventTemplate template) {
    boolean existed = isNull(template.getId())
        ? eventTemplateRepo.existsByEventCode(template.getEventCode())
        : eventTemplateRepo.existsByEventCodeAndIdNot(template.getEventCode(), template.getId());
    assertResourceExisted(!existed, template.getEventCode(), "EventTemplate");
  }

  /**
   * <p>
   * Sets receive settings for event template page.
   * </p>
   * <p>
   * Associates receiver and channel information with templates for tenant-specific settings.
   * </p>
   */
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
