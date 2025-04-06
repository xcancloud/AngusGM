package cloud.xcan.angus.core.gm.application.query.notice;

import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface NoticeSearch {

  Page<Notice> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Notice> clz,
      String... matches);

}
