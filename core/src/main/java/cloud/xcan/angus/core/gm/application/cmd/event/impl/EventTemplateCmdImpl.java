package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.template.EventTemplateRepo;
import cloud.xcan.angus.core.gm.domain.event.template.channel.EventTemplateChannelRepo;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiverRepo;
import cloud.xcan.angus.core.gm.infra.config.EventTemplateCache;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class EventTemplateCmdImpl extends CommCmd<EventTemplate, Long> implements EventTemplateCmd {

  @Resource
  private EventTemplateRepo eventTemplateRepo;

  @Resource
  private EventTemplateQuery eventTemplateQuery;

  @Resource
  private EventTemplateChannelRepo eventTemplateChannelRepo;

  @Resource
  private EventTemplateReceiverRepo eventTemplateReceiverRepo;

  @Resource
  private EventTemplateCache eventTemplateCache;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EventTemplate template) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        assertTrue(!template.getEventType().exceptional || isNotEmpty(template.getEKey()),
            "Exception event eKey is required");
        assertTrue(template.getEventType().exceptional || isNotEmpty(template.getTargetType()),
            "Exception event targetType is required");
        // Check the template event name exists
        eventTemplateQuery.checkEventNameExist(template);
        // Check the template event code and eKey exists
        eventTemplateQuery.checkEventCodeExist(template);
      }

      @Override
      protected IdKey<Long, Object> process() {
        return insert(template);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EventTemplate template) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EventTemplate templateDb;

      @Override
      protected void checkParams() {
        if (nonNull(template.getId())) {
          // Check the template existed
          templateDb = eventTemplateQuery.checkAndFind(template.getId());
          // Check the template event name exists
          eventTemplateQuery.checkEventNameExist(template);
          // Check the template event code and eKey exists
          eventTemplateQuery.checkEventCodeExist(template);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(template.getId())) {
          return add(template);
        }

        deleteTemplateChannel(template, templateDb);
        eventTemplateCache.cacheEventTemplate(template);
        eventTemplateRepo.save(template);
        return IdKey.of(template.getId(), template.getEventName());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        //NOOP
      }

      @Override
      protected Void process() {
        eventTemplateRepo.deleteById(id);
        eventTemplateChannelRepo.deleteAllByTemplateId(id);
        eventTemplateReceiverRepo.deleteAllByTemplateId(id);

        //eventTemplateCache.clearEventTemplates(template);
        // Delete event push ? -> NOOP: The event push will automatically fail after deleting the template
        return null;
      }
    }.execute();
  }

  public void deleteTemplateChannel(EventTemplate template, EventTemplate templateDb) {
    Set<ReceiveChannelType> removedChannelTypes = null;
    if (isEmpty(template.getAllowedChannelTypes())) {
      removedChannelTypes = templateDb.getAllowedChannelTypes();
    } else {
      if (isNotEmpty(templateDb.getAllowedChannelTypes())) {
        templateDb.getAllowedChannelTypes().removeAll(template.getAllowedChannelTypes());
        removedChannelTypes = templateDb.getAllowedChannelTypes();
      }
    }
    if (isNotEmpty(removedChannelTypes)) {
      eventTemplateChannelRepo.deleteAllByTemplateIdAndChannelTypeIn(template.getId(),
          removedChannelTypes.stream().map(ReceiveChannelType::getValue)
              .collect(Collectors.toList()));
    }
  }

  @Override
  protected BaseRepository<EventTemplate, Long> getRepository() {
    return this.eventTemplateRepo;
  }
}
