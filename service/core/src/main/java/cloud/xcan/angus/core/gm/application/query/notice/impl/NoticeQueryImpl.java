package cloud.xcan.angus.core.gm.application.query.notice.impl;

import static cloud.xcan.angus.core.gm.domain.notice.NoticeScope.APP;
import static cloud.xcan.angus.core.gm.domain.notice.NoticeScope.GLOBAL;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeQuery;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.domain.notice.NoticeRepo;
import cloud.xcan.angus.core.gm.domain.notice.NoticeSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of notice query operations.
 * </p>
 * <p>
 * Manages notice retrieval, validation, and application information association.
 * Provides comprehensive notice querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports notice detail retrieval, latest notice queries (global and app-specific),
 * paginated listing, and application information enrichment for comprehensive notice management.
 * </p>
 */
@Slf4j
@Biz
@SummaryQueryRegister(name = "Notice", table = "notice",
    groupByColumns = {"created_date", "scope", "send_type"})
public class NoticeQueryImpl implements NoticeQuery {

  @Resource
  private NoticeRepo noticeRepo;

  @Resource
  private NoticeSearchRepo noticeSearchRepo;

  @Resource
  private AppRepo appRepo;

  /**
   * <p>
   * Retrieves detailed notice information by ID.
   * </p>
   * <p>
   * Fetches complete notice record with application information association.
   * Throws ResourceNotFound exception if notice does not exist.
   * </p>
   */
  @Override
  public Notice detail(Long id) {
    return new BizTemplate<Notice>() {

      @Override
      protected Notice process() {
        Notice notice = noticeRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "Notice"));
        setAppInfo(List.of(notice));
        return notice;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves the latest global notice.
   * </p>
   * <p>
   * Returns the most recent global notice that is not expired.
   * Returns null if no valid global notice exists.
   * </p>
   */
  @Override
  public Notice globalLatest() {
    return new BizTemplate<Notice>() {

      @Override
      protected Notice process() {
        Notice notice = noticeRepo.findFirstByScopeOrderByIdDesc(GLOBAL);
        if (nonNull(notice) && !notice.isExpired()) {
          setAppInfo(List.of(notice));
          return notice;
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves the latest notice for specific application.
   * </p>
   * <p>
   * Returns the most recent app-specific notice or falls back to global notice.
   * Prioritizes app-specific notices over global notices based on timing.
   * </p>
   */
  @Override
  public Notice appLatest(Long appId) {
    return new BizTemplate<Notice>() {

      @Override
      protected Notice process() {
        Notice appNotice = noticeRepo.findFirstByAppIdAndScopeOrderByIdDesc(appId, APP);
        Notice globalNotice = noticeRepo.findFirstByScopeOrderByIdDesc(GLOBAL);
        Notice finalNotice;

        // Return to global notice when there is no app notice
        if (isNull(appNotice) || appNotice.isExpired()) {
          finalNotice = nonNull(globalNotice) && !globalNotice.isExpired() ? globalNotice : null;
        } else {
          finalNotice = nonNull(globalNotice) && !globalNotice.isExpired()
              && globalNotice.getTimingDate().isBefore(appNotice.getTimingDate())
              ? globalNotice : appNotice;
        }

        if (nonNull(finalNotice)) {
          setAppInfo(List.of(finalNotice));
        }
        return finalNotice;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves notices with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Enriches results with application information for comprehensive display.
   * </p>
   */
  @Override
  public Page<Notice> list(GenericSpecification<Notice> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Notice>>() {

      @Override
      protected Page<Notice> process() {
        Page<Notice> page = fullTextSearch
            ? noticeSearchRepo.find(spec.getCriteria(), pageable, Notice.class, match)
            : noticeRepo.findAll(spec, pageable);
        setAppInfo(page.getContent());
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates timing parameters for notice sending.
   * </p>
   * <p>
   * Ensures timing date is provided when send type is TIMING_SEND.
   * Throws ProtocolException if timing date is missing for timing send.
   * </p>
   */
  @Override
  public void checkAppSendTimingParam(Notice notice) {
    if (notice.getSendType().equals(SentType.TIMING_SEND) && isNull(notice.getTimingDate())) {
      throw ProtocolException.of(PARAM_MISSING_T, PARAM_MISSING_KEY,
          new Object[]{"sendTimingDate"});
    }
  }

  /**
   * <p>
   * Sets application information for notice list.
   * </p>
   * <p>
   * Loads application details and associates with notices for complete information.
   * </p>
   */
  @Override
  public void setAppInfo(List<Notice> notices) {
    if (isEmpty(notices)) {
      return;
    }
    Map<Long, App> appMap = appRepo.findAllByIdIn(notices.stream().map(Notice::getId)
        .collect(Collectors.toSet())).stream().collect(Collectors.toMap(App::getId, x -> x));
    if (isNotEmpty(appMap)) {
      for (Notice notice : notices) {
        App app = appMap.get(notice.getAppId());
        if (nonNull(app)) {
          notice.setAppCode(app.getCode()).setAppName(app.getName())
              .setEditionType(app.getEditionType());
        }
      }
    }
  }
}
