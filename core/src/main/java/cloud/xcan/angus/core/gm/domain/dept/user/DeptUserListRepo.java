package cloud.xcan.angus.core.gm.domain.dept.user;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DeptUserListRepo extends CustomBaseRepository<DeptUser> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<DeptUser> mainClz, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
