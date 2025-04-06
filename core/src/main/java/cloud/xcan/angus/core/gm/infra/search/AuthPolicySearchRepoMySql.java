package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyListRepo;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicySearchRepo;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class AuthPolicySearchRepoMySql extends SimpleSearchRepository<AuthPolicy> implements
    AuthPolicySearchRepo {

  @Resource
  private AuthPolicyListRepo authPolicyListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<AuthPolicy> mainClz,
      Object[] params, String... matches) {
    return authPolicyListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz, "auth_policy",
        matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return authPolicyListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return super.getSearchMode();
  }

}
