package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplateRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of email template command service
 */
@Biz
public class EmailTemplateCmdImpl extends CommCmd<EmailTemplate, Long> implements EmailTemplateCmd {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailTemplate create(EmailTemplate template) {
    return new BizTemplate<EmailTemplate>() {
      @Override
      protected void checkParams() {
        if (emailTemplateRepo.existsByCode(template.getCode())) {
          throw ResourceExisted.of("邮件模板编码「{0}」已存在", new Object[]{template.getCode()});
        }
      }

      @Override
      protected EmailTemplate process() {
        if (template.getStatus() == null) {
          template.setStatus("已启用");
        }
        insert(template);
        return template;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailTemplate update(EmailTemplate template) {
    return new BizTemplate<EmailTemplate>() {
      EmailTemplate templateDb;

      @Override
      protected void checkParams() {
        templateDb = emailTemplateQuery.findAndCheck(template.getId());
        
        if (template.getCode() != null && !template.getCode().equals(templateDb.getCode())) {
          if (emailTemplateRepo.existsByCodeAndIdNot(template.getCode(), template.getId())) {
            throw ResourceExisted.of("邮件模板编码「{0}」已存在", new Object[]{template.getCode()});
          }
        }
      }

      @Override
      protected EmailTemplate process() {
        update(template, templateDb);
        return templateDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailTemplate updateStatus(Long id, String status) {
    return new BizTemplate<EmailTemplate>() {
      EmailTemplate templateDb;

      @Override
      protected void checkParams() {
        templateDb = emailTemplateQuery.findAndCheck(id);
      }

      @Override
      protected EmailTemplate process() {
        templateDb.setStatus(status);
        emailTemplateRepo.save(templateDb);
        return templateDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        emailTemplateQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        emailTemplateRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailTemplate, Long> getRepository() {
    return emailTemplateRepo;
  }
}

