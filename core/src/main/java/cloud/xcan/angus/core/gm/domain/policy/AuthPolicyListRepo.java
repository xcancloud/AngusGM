package cloud.xcan.angus.core.gm.domain.policy;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;


public interface AuthPolicyListRepo extends CustomBaseRepository<AuthPolicy> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<AuthPolicy> mainClz, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
