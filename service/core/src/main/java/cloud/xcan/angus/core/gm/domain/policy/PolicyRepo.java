package cloud.xcan.angus.core.gm.domain.policy;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Policy repository interface
 */
@NoRepositoryBean
public interface PolicyRepo extends BaseRepository<Policy, Long> {

  /**
   * Check if name exists
   */
  boolean existsByName(String name);

  /**
   * Check if name exists excluding specific id
   */
  boolean existsByNameAndIdNot(String name, Long id);

  /**
   * Check if code exists
   */
  boolean existsByCode(String code);

  /**
   * Check if code exists excluding specific id
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * Count policies by status
   */
  long countByStatus(PolicyStatus status);

  /**
   * Count system roles
   */
  long countByIsSystemTrue();

  /**
   * Count custom roles
   */
  long countByIsSystemFalse();

  /**
   * Find policies by appId
   */
  List<Policy> findByAppId(String appId);

  /**
   * Find default policy by appId
   */
  Policy findByAppIdAndIsDefaultTrue(String appId);

  /**
   * Count users by policy id (through authorization)
   */
  long countUsersByPolicyId(Long policyId);
}
