package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.EVENT_TEMPLATE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
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

/**
 * Implementation of event template command operations for managing event templates.
 * 
 * <p>This class provides comprehensive functionality for event template management including:</p>
 * <ul>
 *   <li>Creating and configuring event templates</li>
 *   <li>Managing template-channel associations</li>
 *   <li>Handling template receiver configurations</li>
 *   <li>Caching template configurations for performance</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures proper template management with validation,
 * caching, and audit trail maintenance.</p>
 */
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
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates a new event template with comprehensive validation.
   * 
   * <p>This method performs template creation including:</p>
   * <ul>
   *   <li>Validating exceptional event requirements</li>
   *   <li>Checking template name uniqueness</li>
   *   <li>Validating event code and key uniqueness</li>
   *   <li>Creating template configuration</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param template Event template configuration to create
   * @return Created template identifier
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EventTemplate template) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Validate exceptional event requirements
        assertTrue(!template.getEventType().exceptional || isNotEmpty(template.getEKey()),
            "Exception event eKey is required");
        assertTrue(template.getEventType().exceptional || isNotEmpty(template.getTargetType()),
            "Exception event targetType is required");
        // Validate template event name uniqueness
        eventTemplateQuery.checkEventNameExist(template);
        // Validate template event code and key uniqueness
        eventTemplateQuery.checkEventCodeExist(template);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Create template and record audit log
        IdKey<Long, Object> idKey = insert(template);
        operationLogCmd.add(EVENT_TEMPLATE, template, CREATED);
        return idKey;
      }
    }.execute();
  }

  /**
   * Replaces an event template configuration or creates a new one.
   * 
   * <p>This method performs template replacement including:</p>
   * <ul>
   *   <li>Validating template existence if ID is provided</li>
   *   <li>Checking name and code uniqueness</li>
   *   <li>Managing template-channel associations</li>
   *   <li>Updating template cache</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param template Event template configuration to replace
   * @return Template identifier with name information
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EventTemplate template) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EventTemplate templateDb;

      @Override
      protected void checkParams() {
        if (nonNull(template.getId())) {
          // Validate template exists
          templateDb = eventTemplateQuery.checkAndFind(template.getId());
          // Validate template event name uniqueness
          eventTemplateQuery.checkEventNameExist(template);
          // Validate template event code and key uniqueness
          eventTemplateQuery.checkEventCodeExist(template);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Create new template if no existing template found
        if (isNull(template.getId())) {
          return add(template);
        }

        // Manage template-channel associations
        deleteTemplateChannel(template, templateDb);
        // Update template cache
        eventTemplateCache.cacheEventTemplate(template);
        eventTemplateRepo.save(template);
        // Record operation audit log
        operationLogCmd.add(EVENT_TEMPLATE, template, UPDATED);
        return IdKey.of(template.getId(), template.getEventName());
      }
    }.execute();
  }

  /**
   * Deletes an event template by its identifier.
   * 
   * <p>This method performs template deletion including:</p>
   * <ul>
   *   <li>Validating template existence</li>
   *   <li>Removing template-channel associations</li>
   *   <li>Removing template-receiver associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param id Template identifier to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      EventTemplate templateDb;

      @Override
      protected void checkParams() {
        templateDb = eventTemplateQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        // Delete template and related associations
        eventTemplateRepo.deleteById(id);
        eventTemplateChannelRepo.deleteAllByTemplateId(id);
        eventTemplateReceiverRepo.deleteAllByTemplateId(id);

        // Note: Event push will automatically fail after template deletion
        // eventTemplateCache.clearEventTemplates(template);

        // Record operation audit log
        operationLogCmd.add(EVENT_TEMPLATE, templateDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Manages template-channel associations during template updates.
   * 
   * <p>This method handles channel association updates including:</p>
   * <ul>
   *   <li>Identifying removed channel types</li>
   *   <li>Removing outdated channel associations</li>
   *   <li>Maintaining channel association consistency</li>
   * </ul>
   * 
   * @param template New template configuration
   * @param templateDb Existing template configuration
   */
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
      // Remove outdated channel associations
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
