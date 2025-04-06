package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSearch {

  Page<User> search(Set<SearchCriteria> criteria, Pageable pageable, Class<User> userClass,
      String... matches);
}
