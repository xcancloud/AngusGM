package cloud.xcan.angus.core.gm.application.query.policy.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicySearch;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicySearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class AuthPolicySearchImpl implements AuthPolicySearch {

  @Resource
  private AuthPolicySearchRepo authPolicySearchRepo;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  /**
   * Query user-defined and platform preset policies.
   */
  @Override
  public Page<AuthPolicy> search(Set<SearchCriteria> criteria, Pageable pageable,
      String... matches) {
    return new BizTemplate<Page<AuthPolicy>>(false) {

      @Override
      protected Page<AuthPolicy> process() {
        Page<AuthPolicy> page = authPolicySearchRepo.find(criteria, pageable,
            AuthPolicy.class, matches);

        if (page.hasContent()) {
          authPolicyQuery.setAppInfo(page.getContent());
        }
        return page;
      }
    }.execute();
  }
}
