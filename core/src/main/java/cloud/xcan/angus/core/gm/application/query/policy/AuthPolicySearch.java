package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AuthPolicySearch {

  Page<AuthPolicy> search(Set<SearchCriteria> criteria, Pageable pageable, String... matches);

}
