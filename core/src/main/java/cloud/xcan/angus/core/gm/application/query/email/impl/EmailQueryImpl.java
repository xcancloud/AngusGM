package cloud.xcan.angus.core.gm.application.query.email.impl;

import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.NO_TEMPLATE_BIZ_CONFIG_CODE;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.NO_TEMPLATE_BIZ_CONFIG_T;
import static cloud.xcan.angus.core.gm.domain.EmailMessage.TOO_MANY_ATTACHMENT;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.EmailConstant;
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
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

  @Override
  public Page<Email> find(Specification<Email> spec, Pageable pageable) {
    return new BizTemplate<Page<Email>>() {

      @Override
      protected Page<Email> process() {
        return emailRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Email checkAndFind(Long id) {
    return new BizTemplate<Email>() {

      @Override
      protected Email process() {
        return emailRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "Email"));
      }
    }.execute();
  }

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

  @Override
  public List<Email> findTenantEmailInPending(int count) {
    return emailRepo.findTenantEmailInPending(count);
  }

  @Override
  public List<Email> findPlatformEmailInPending(int count) {
    return emailRepo.findPlatformEmailInPending(count);
  }

  /**
   * Check that the number of attachments cannot exceed the limit
   * {@link EmailConstant#MAX_ATTACHMENT_NUM}
   */
  @Override
  public void checkAttachmentQuota(Email email) {
    assertTrue(isEmpty(email.getAttachmentData())
            || email.getAttachmentData().size() <= EmailConstant.MAX_ATTACHMENT_NUM,
        TOO_MANY_ATTACHMENT);
  }

}
