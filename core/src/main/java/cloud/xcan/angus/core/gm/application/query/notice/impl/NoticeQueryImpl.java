package cloud.xcan.angus.core.gm.application.query.notice.impl;

import static cloud.xcan.angus.core.gm.domain.notice.NoticeScope.APP;
import static cloud.xcan.angus.core.gm.domain.notice.NoticeScope.GLOBAL;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.PARAM_MISSING_T;
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
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.CommProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


@Slf4j
@Biz
@SummaryQueryRegister(name = "Notice", table = "notice",
    groupByColumns = {"created_date", "scope", "send_type"})
public class NoticeQueryImpl implements NoticeQuery {

  @Resource
  private NoticeRepo noticeRepo;

  @Resource
  private AppRepo appRepo;

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

  @Override
  public Page<Notice> find(Specification<Notice> spec, PageRequest pageable) {
    return new BizTemplate<Page<Notice>>() {

      @Override
      protected Page<Notice> process() {
        Page<Notice> page = noticeRepo.findAll(spec, pageable);
        setAppInfo(page.getContent());
        return page;
      }
    }.execute();
  }

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

  @Override
  public void checkAppSendTimingParam(Notice notice) {
    if (notice.getSendType().equals(SentType.TIMING_SEND) && isNull(notice.getTimingDate())) {
      throw CommProtocolException.of(PARAM_MISSING_T, PARAM_MISSING_KEY,
          new Object[]{"sendTimingDate"});
    }
  }

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
