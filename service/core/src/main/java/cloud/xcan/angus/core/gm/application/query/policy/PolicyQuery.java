package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Policy query service interface
 */
public interface PolicyQuery {

  /**
   * Find policy by id and check existence
   */
  Policy findAndCheck(Long id);

  /**
   * Find policies with pagination
   */
  Page<Policy> find(GenericSpecification<Policy> spec, PageRequest pageable,
                    boolean fullTextSearch, String[] match);

  /**
   * Count total policies
   */
  long countTotal();

  /**
   * Count system roles
   */
  long countSystemRoles();

  /**
   * Count custom roles
   */
  long countCustomRoles();

  /**
   * Count total users with policies
   */
  long countTotalUsers();

  /**
   * Find users by policy id with pagination
   */
  org.springframework.data.domain.Page<cloud.xcan.angus.core.gm.domain.user.User> findUsersByPolicyId(
      Long policyId, org.springframework.data.domain.PageRequest pageable);

  /**
   * Count users by policy id
   */
  long countUsersByPolicyId(Long policyId);
}
