package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateReceiverCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiverRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of event template receiver command operations for managing template-receiver
 * associations.
 *
 * <p>This class provides comprehensive functionality for template-receiver management
 * including:</p>
 * <ul>
 *   <li>Managing event template and receiver associations</li>
 *   <li>Replacing receiver configurations for templates</li>
 *   <li>Handling receiver type and identifier assignments</li>
 *   <li>Validating receiver configuration requirements</li>
 * </ul>
 *
 * <p>The implementation ensures proper template-receiver relationship management
 * with validation and configuration consistency.</p>
 */
@org.springframework.stereotype.Service
public class EventTemplateReceiverCmdImpl extends CommCmd<EventTemplateReceiver, Long>
    implements EventTemplateReceiverCmd {

  @Resource
  private EventTemplateReceiverRepo eventTemplateReceiverRepo;

  @Resource
  private EventTemplateQuery eventTemplateQuery;

  /**
   * Replaces receiver associations for an event template.
   *
   * <p>This method performs receiver replacement including:</p>
   * <ul>
   *   <li>Validating template existence</li>
   *   <li>Removing existing receiver associations</li>
   *   <li>Creating new receiver associations</li>
   *   <li>Validating receiver configuration requirements</li>
   * </ul>
   *
   * @param receiver Event template receiver configuration to replace
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void receiverReplace(EventTemplateReceiver receiver) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Validate event template exists
        eventTemplateQuery.checkAndFind(receiver.getTemplateId());
      }

      @Override
      protected Void process() {
        // Remove existing template-receiver associations
        eventTemplateReceiverRepo.deleteAllByTemplateId(receiver.getTemplateId());

        // Create new template-receiver associations if configuration is valid
        if (isNotEmpty(receiver.getNoticeTypes())
            && (isNotEmpty(receiver.getReceiverTypes()) || isNotEmpty(receiver.getReceiverIds()))) {
          insert0(receiver);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EventTemplateReceiver, Long> getRepository() {
    return eventTemplateReceiverRepo;
  }
}
