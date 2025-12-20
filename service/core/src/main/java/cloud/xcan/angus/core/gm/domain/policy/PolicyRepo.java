package cloud.xcan.angus.core.gm.domain.policy;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Policy repository interface
 */
@NoRepositoryBean
public interface PolicyRepo extends BaseRepository<Policy, Long> {

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
   * Count policies by effect
   */
  long countByEffect(PolicyEffect effect);
}
