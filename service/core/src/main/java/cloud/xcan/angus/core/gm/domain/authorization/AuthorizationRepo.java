package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Authorization repository interface
 */
@NoRepositoryBean
public interface AuthorizationRepo extends BaseRepository<Authorization, Long> {

  /**
   * Find authorizations by status with pagination
   */
  Page<Authorization> findByStatus(AuthorizationStatus status, Pageable pageable);

  /**
   * Find authorizations by subject type with pagination
   */
  Page<Authorization> findBySubjectType(SubjectType subjectType, Pageable pageable);

  /**
   * Find authorizations by subject id with pagination
   */
  Page<Authorization> findBySubjectId(Long subjectId, Pageable pageable);

  /**
   * Find authorizations by policy id with pagination
   */
  Page<Authorization> findByPolicyId(Long policyId, Pageable pageable);

  /**
   * Count authorizations by status
   */
  long countByStatus(AuthorizationStatus status);

  /**
   * Count authorizations by subject type
   */
  long countBySubjectType(SubjectType subjectType);

  /**
   * Check if authorization exists for subject and policy
   */
  boolean existsBySubjectTypeAndSubjectIdAndPolicyId(SubjectType subjectType, Long subjectId, Long policyId);
}
