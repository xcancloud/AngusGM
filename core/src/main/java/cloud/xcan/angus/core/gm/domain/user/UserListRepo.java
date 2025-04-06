package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserListRepo extends CustomBaseRepository<User> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria, Class<User> mainClz,
      String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
