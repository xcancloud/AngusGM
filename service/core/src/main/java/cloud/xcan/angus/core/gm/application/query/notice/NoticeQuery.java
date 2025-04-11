package cloud.xcan.angus.core.gm.application.query.notice;

import cloud.xcan.angus.core.gm.domain.notice.Notice;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


public interface NoticeQuery {

  Notice detail(Long id);

  Notice globalLatest();

  Notice appLatest(Long appId);

  Page<Notice> find(Specification<Notice> spec, PageRequest pageable);

  void checkAppSendTimingParam(Notice notice);

  void setAppInfo(List<Notice> notices);
}
