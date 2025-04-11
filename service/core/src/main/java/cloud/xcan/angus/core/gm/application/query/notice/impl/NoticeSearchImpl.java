package cloud.xcan.angus.core.gm.application.query.notice.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeQuery;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeSearch;
import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.domain.notice.NoticeSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class NoticeSearchImpl implements NoticeSearch {

  @Resource
  private NoticeSearchRepo noticeSearchRepo;

  @Resource
  private NoticeQuery noticeQuery;

  @Override
  public Page<Notice> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Notice> clz, String... matches) {
    return new BizTemplate<Page<Notice>>() {

      @Override
      protected Page<Notice> process() {
        Page<Notice> page = noticeSearchRepo.find(criteria, pageable, clz, matches);
        noticeQuery.setAppInfo(page.getContent());
        return page;
      }
    }.execute();
  }

}
