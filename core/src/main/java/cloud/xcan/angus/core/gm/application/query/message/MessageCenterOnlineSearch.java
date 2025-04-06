package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnline;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageCenterOnlineSearch {

  Page<MessageCenterOnline> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<MessageCenterOnline> clz, String... matches);

}
