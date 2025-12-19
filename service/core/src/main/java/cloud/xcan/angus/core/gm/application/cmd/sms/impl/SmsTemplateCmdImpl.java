package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.toSmsTemplate;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.SMS_TEMPLATE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsTemplateCmd;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplateRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of SMS template command operations.
 * </p>
 * <p>
 * Manages SMS template lifecycle including initialization, updates, and synchronization with SMS
 * channels.
 * </p>
 * <p>
 * Provides template initialization for new channels and template updates with audit logging.
 * </p>
 */
@org.springframework.stereotype.Service
public class SmsTemplateCmdImpl extends CommCmd<SmsTemplate, Long> implements SmsTemplateCmd {

  @Resource
  private SmsTemplateRepo smsTemplateRepo;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Initializes SMS templates for new channels.
   * </p>
   * <p>
   * Creates template instances for each channel using default templates. Only initializes templates
   * for channels that don't already have templates.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void init(List<SmsChannel> channels) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Get default templates for initialization
        List<SmsTemplate> initTemplates = smsTemplateRepo.findByChannelId(-1L);
        if (isEmpty(initTemplates)) {
          return null;
        }
        List<SmsTemplate> templates = new ArrayList<>();
        for (SmsChannel channel : channels) {
          // Only initialize templates for channels that don't have any templates
          if (smsTemplateRepo.countByChannelId(channel.getId()) <= 0) {
            for (SmsTemplate initSmsTemplate : initTemplates) {
              templates.add(toSmsTemplate(uidGenerator.getUID(), channel, initSmsTemplate));
            }
            batchInsert(templates);
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates SMS template configuration.
   * </p>
   * <p>
   * Updates template settings and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(SmsTemplate template) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SmsTemplate templateDb = updateOrNotFound(template);
        operationLogCmd.add(SMS_TEMPLATE, templateDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SmsTemplate, Long> getRepository() {
    return this.smsTemplateRepo;
  }
}
