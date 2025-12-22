package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplateRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of email template query service
 */
@Biz
public class EmailTemplateQueryImpl implements EmailTemplateQuery {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  @Override
  public EmailTemplate findAndCheck(Long id) {
    return new BizTemplate<EmailTemplate>() {
      @Override
      protected EmailTemplate process() {
        return emailTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("邮件模板未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<EmailTemplate> find(GenericSpecification<EmailTemplate> spec, PageRequest pageable) {
    return new BizTemplate<Page<EmailTemplate>>() {
      @Override
      protected Page<EmailTemplate> process() {
        return emailTemplateRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return emailTemplateRepo.existsByCode(code);
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, Long id) {
    return emailTemplateRepo.existsByCodeAndIdNot(code, id);
  }
}

