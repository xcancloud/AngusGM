package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplateRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

/**
 * <p>
 * Implementation of SMS template query operations.
 * </p>
 * <p>
 * Manages SMS template retrieval, validation, and listing.
 * Provides comprehensive SMS template querying with pagination support.
 * </p>
 * <p>
 * Supports SMS template detail retrieval and paginated listing
 * for comprehensive SMS template administration.
 * </p>
 */
@Biz
public class SmsTemplateQueryImpl implements SmsTemplateQuery {

  @Resource
  private SmsTemplateRepo smsTemplateRepo;

  /**
   * <p>
   * Retrieves detailed SMS template information by ID.
   * </p>
   * <p>
   * Fetches complete SMS template record with existence validation.
   * Throws ResourceNotFound exception if SMS template does not exist.
   * </p>
   */
  @Override
  public SmsTemplate detail(Long id) {
    return new BizTemplate<SmsTemplate>() {

      @Override
      protected SmsTemplate process() {
        return smsTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "SmsTemplate"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves SMS templates with optional filtering and pagination.
   * </p>
   * <p>
   * Supports specification-based filtering and pagination.
   * Returns paginated SMS template results.
   * </p>
   */
  @Override
  public Page<SmsTemplate> list(Specification<SmsTemplate> spec, PageRequest pageable) {
    return new BizTemplate<Page<SmsTemplate>>() {
      @Override
      protected Page<SmsTemplate> process() {
        return smsTemplateRepo.findAll(spec, pageable);
      }
    }.execute();
  }

}
