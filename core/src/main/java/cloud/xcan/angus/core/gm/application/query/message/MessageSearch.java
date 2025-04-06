package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MessageSearch {

  Page<MessageInfo> search(Set<SearchCriteria> criteria, Pageable pageable, Class<MessageInfo> clz,
      String... matches);

}
