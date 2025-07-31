package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.EMAIL_TEMPLATE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailTemplateCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplateRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of email template command operations for managing email templates.
 * 
 * <p>This class provides functionality for email template management including:</p>
 * <ul>
 *   <li>Updating email template configurations</li>
 *   <li>Managing template content and parameters</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures proper template management with audit trail maintenance.</p>
 */
@Biz
public class EmailTemplateCmdImpl extends CommCmd<EmailTemplate, Long> implements EmailTemplateCmd {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Updates an email template with comprehensive validation.
   * 
   * <p>This method performs template update including:</p>
   * <ul>
   *   <li>Validating template existence</li>
   *   <li>Updating template configuration</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param emailTemplate Email template to update
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(EmailTemplate emailTemplate) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Update template and record audit log
        EmailTemplate templateDb = updateOrNotFound(emailTemplate);
        operationLogCmd.add(EMAIL_TEMPLATE, templateDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailTemplate, Long> getRepository() {
    return this.emailTemplateRepo;
  }
}
