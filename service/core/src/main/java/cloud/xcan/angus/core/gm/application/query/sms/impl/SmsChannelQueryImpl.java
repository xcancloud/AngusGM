package cloud.xcan.angus.core.gm.application.query.sms.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannelRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * <p>
 * Implementation of SMS channel query operations.
 * </p>
 * <p>
 * Manages SMS channel retrieval, validation, and status checking.
 * Provides comprehensive SMS channel querying with enabled status support.
 * </p>
 * <p>
 * Supports SMS channel detail retrieval, paginated listing, and enabled channel queries
 * for comprehensive SMS channel administration.
 * </p>
 */
@Biz
public class SmsChannelQueryImpl implements SmsChannelQuery {

  @Resource
  private SmsChannelRepo smsChannelRepo;

  /**
   * <p>
   * Retrieves detailed SMS channel information by ID.
   * </p>
   * <p>
   * Fetches complete SMS channel record with existence validation.
   * Throws ResourceNotFound exception if SMS channel does not exist.
   * </p>
   */
  @Override
  public SmsChannel detail(Long id) {
    return new BizTemplate<SmsChannel>() {

      @Override
      protected SmsChannel process() {
        return smsChannelRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "SmsChannel"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves SMS channels with optional filtering and pagination.
   * </p>
   * <p>
   * Supports specification-based filtering and pagination.
   * Returns paginated SMS channel results.
   * </p>
   */
  @Override
  public Page<SmsChannel> list(Specification<SmsChannel> spec, Pageable pageable) {
    return new BizTemplate<Page<SmsChannel>>() {

      @Override
      protected Page<SmsChannel> process() {
        return smsChannelRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves enabled SMS channel.
   * </p>
   * <p>
   * Returns the first enabled SMS channel found.
   * Returns null if no enabled SMS channel exists.
   * </p>
   */
  @Override
  public SmsChannel findEnabled() {
    List<SmsChannel> smsChannels = smsChannelRepo.findByEnabled(true);
    return isEmpty(smsChannels) ? null : smsChannels.get(0);
  }

}
