package cloud.xcan.angus.core.gm.domain.policy.org;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;


public interface AuthUserAssocPolicyListRepo extends CustomBaseRepository<AuthPolicy> {

  StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<AuthPolicy> mainClz, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
