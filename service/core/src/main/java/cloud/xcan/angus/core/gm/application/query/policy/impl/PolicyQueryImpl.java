package cloud.xcan.angus.core.gm.application.query.policy.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of policy query service
 */
@Biz
public class PolicyQueryImpl implements PolicyQuery {

  @Resource
  private PolicyRepo policyRepo;

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private UserRepo userRepo;

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
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        // Count distinct users who have at least one authorization
        return authorizationRepo.countDistinctUsers();
      }
    }.execute();
  }

  @Override
  public Page<User> findUsersByPolicyId(Long policyId, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        // Find authorizations for this policy with USER subject type
        Page<cloud.xcan.angus.core.gm.domain.authorization.Authorization> authPage = 
            authorizationRepo.findByPolicyIdAndSubjectType(policyId, SubjectType.USER, pageable);
        
        // Extract user IDs
        List<Long> userIds = authPage.getContent().stream()
            .map(cloud.xcan.angus.core.gm.domain.authorization.Authorization::getSubjectId)
            .collect(Collectors.toList());
        
        if (userIds.isEmpty()) {
          return org.springframework.data.domain.Page.empty(pageable);
        }
        
        // Find users by IDs
        List<User> users = userRepo.findAllById(userIds);
        
        // Create page result
        return new org.springframework.data.domain.PageImpl<>(
            users, pageable, authPage.getTotalElements());
      }
    }.execute();
  }

  @Override
  public long countUsersByPolicyId(Long policyId) {
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        return authorizationRepo.countByPolicyIdAndSubjectType(policyId, SubjectType.USER);
      }
    }.execute();
  }
}
