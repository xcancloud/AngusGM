package cloud.xcan.angus.core.gm.application.query.email.impl;

import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.NO_TEMPLATE_BIZ_CONFIG_CODE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.NO_TEMPLATE_BIZ_CONFIG_T;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TOO_MANY_ATTACHMENT;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.EmailConstant;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.biz.EmailTemplateBiz;
import cloud.xcan.angus.core.gm.domain.email.biz.EmailTemplateBizRepo;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * <p>
 * Implementation of email query operations.
 * </p>
 * <p>
 * Manages email retrieval, template validation, and attachment quota checking. Provides
 * comprehensive email querying with summary support.
 * </p>
 * <p>
 * Supports email detail retrieval, template validation, pending email queries, and attachment quota
 * validation for comprehensive email management.
 * </p>
 */
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "Email", table = "email", isMultiTenantCtrl = false,
    groupByColumns = {"actual_send_date", "send_status", "urgent", "verification_code",
        "batch"})
public class EmailQueryImpl implements EmailQuery {

  @Resource
  private EmailRepo emailRepo;
  @Resource
  private EmailTemplateBizRepo emailTemplateBizRepo;
  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  /**
   * <p>
   * Retrieves detailed email information by ID.
   * </p>
   * <p>
   * Fetches complete email record with all associated information. Throws ResourceNotFound
   * exception if email does not exist.
   * </p>
   */
  @Override
  public Email detail(Long id) {
    return new BizTemplate<Email>() {

      @Override
      protected Email process() {
        return emailRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "Email"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves emails with specification-based filtering.
   * </p>
   * <p>
   * Supports dynamic filtering and pagination for comprehensive email management.
   * </p>
   */
  @Override
  public Page<Email> list(Specification<Email> spec, Pageable pageable) {
    return new BizTemplate<Page<Email>>() {

      @Override
      protected Page<Email> process() {
        return emailRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves email template for email.
   * </p>
   * <p>
   * Verifies template exists and is properly configured for the email business key. Throws
   * appropriate exception if template configuration is invalid.
   * </p>
   */
  @Override
  public EmailTemplate checkAndFindTemplate(Email email) {
    EmailTemplate templateDb = null;
    EmailTemplateBiz templateBiz = emailTemplateBizRepo.findByBizKey(email.getBizKey());
    if (nonNull(templateBiz)) {
      templateDb = emailTemplateQuery.check(email.getLanguage(), templateBiz.getTemplateCode());
    }
    assertTrue(nonNull(templateBiz) && nonNull(templateDb), NO_TEMPLATE_BIZ_CONFIG_CODE,
        NO_TEMPLATE_BIZ_CONFIG_T, new Object[]{email.getBizKey().getValue()});
    templateDb.setTemplateBiz(templateBiz);
    return templateDb;
  }

  /**
   * <p>
   * Retrieves pending tenant emails for processing.
   * </p>
   * <p>
   * Returns specified number of pending emails for tenant processing.
   * </p>
   */
  @Override
  public List<Email> findTenantEmailInPending(int count) {
    return emailRepo.findTenantEmailInPending(count);
  }

  /**
   * <p>
   * Retrieves pending platform emails for processing.
   * </p>
   * <p>
   * Returns specified number of pending emails for platform processing.
   * </p>
   */
  @Override
  public List<Email> findPlatformEmailInPending(int count) {
    return emailRepo.findPlatformEmailInPending(count);
  }

  /**
   * <p>
   * Validates email attachment quota limits.
   * </p>
   * <p>
   * Verifies that the number of attachments does not exceed the maximum limit. Throws appropriate
   * exception if quota is exceeded.
   * </p>
   */
  @Override
  public void checkAttachmentQuota(Email email) {
    assertTrue(isEmpty(email.getAttachmentData())
            || email.getAttachmentData().size() <= EmailConstant.MAX_ATTACHMENT_NUM,
        TOO_MANY_ATTACHMENT);
  }

}
