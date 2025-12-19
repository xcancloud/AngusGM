package cloud.xcan.angus.core.gm.application.query.email.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;


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

/**
 * <p>
 * Implementation of email template query operations.
 * </p>
 * <p>
 * Manages email template retrieval, validation, and language-specific queries. Provides
 * comprehensive email template querying with multi-language support.
 * </p>
 * <p>
 * Supports template detail retrieval, language-based validation, and specification-based filtering
 * for comprehensive email template management.
 * </p>
 */
@org.springframework.stereotype.Service
public class EmailTemplateQueryImpl implements EmailTemplateQuery {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  /**
   * <p>
   * Retrieves detailed email template information by ID.
   * </p>
   * <p>
   * Fetches complete template record with all associated information. Throws ResourceNotFound
   * exception if template does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves email templates with specification-based filtering.
   * </p>
   * <p>
   * Supports dynamic filtering and pagination for comprehensive template management.
   * </p>
   */
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

  /**
   * <p>
   * Validates and retrieves email template by language and code.
   * </p>
   * <p>
   * Verifies template exists for the specified language and code combination. Throws
   * ResourceNotFound exception if template does not exist.
   * </p>
   */
  @Override
  public EmailTemplate check(SupportedLanguage language, String code) {
    EmailTemplate template = emailTemplateRepo.findByLanguageAndCode(language, code);
    assertResourceNotFound(template, code + "-" + language, "EmailTemplate");
    return template;
  }
}
