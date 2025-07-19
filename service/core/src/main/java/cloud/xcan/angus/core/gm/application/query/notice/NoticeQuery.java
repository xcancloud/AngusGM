package cloud.xcan.angus.core.gm.application.query.notice;

import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface NoticeQuery {

  Notice detail(Long id);

  Notice globalLatest();

  Notice appLatest(Long appId);

  Page<Notice> list(GenericSpecification<Notice> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  void checkAppSendTimingParam(Notice notice);

  void setAppInfo(List<Notice> notices);

}
