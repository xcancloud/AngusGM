package cloud.xcan.angus.core.gm.application.query.policy.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of policy query service
 */
@Biz
public class PolicyQueryImpl implements PolicyQuery {

  @Resource
  private PolicyRepo policyRepo;

  @Override
  public Policy findAndCheck(Long id) {
    return new BizTemplate<Policy>() {
      @Override
      protected Policy process() {
        return policyRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("策略未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Policy> find(GenericSpecification<Policy> spec, PageRequest pageable,
                           boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Policy>>() {
      @Override
      protected Page<Policy> process() {
        return policyRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public long countTotal() {
    return policyRepo.count();
  }

  @Override
  public long countSystemRoles() {
    return policyRepo.countByIsSystemTrue();
  }

  @Override
  public long countCustomRoles() {
    return policyRepo.countByIsSystemFalse();
  }

  @Override
  public long countTotalUsers() {
    // TODO: Implement user count query
    return 0L;
  }
}
