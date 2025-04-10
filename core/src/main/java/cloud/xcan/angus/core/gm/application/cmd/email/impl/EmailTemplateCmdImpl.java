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


@Biz
public class EmailTemplateCmdImpl extends CommCmd<EmailTemplate, Long> implements
    EmailTemplateCmd {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(EmailTemplate emailTemplate) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
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
