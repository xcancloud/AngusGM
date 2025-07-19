package cloud.xcan.angus.core.gm.application.query.email.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplateRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class EmailTemplateQueryImpl implements EmailTemplateQuery {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  @Override
  public EmailTemplate detail(Long id) {
    return new BizTemplate<EmailTemplate>() {

      @Override
      protected EmailTemplate process() {
        return emailTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "EmailTemplate"));
      }
    }.execute();
  }

  @Override
  public Page<EmailTemplate> list(Specification<EmailTemplate> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<EmailTemplate>>() {

      @Override
      protected Page<EmailTemplate> process() {
        return emailTemplateRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public EmailTemplate check(SupportedLanguage language, String code) {
    EmailTemplate template = emailTemplateRepo.findByLanguageAndCode(language, code);
    assertResourceNotFound(template, code + "-" + language, "EmailTemplate");
    return template;
  }
}
