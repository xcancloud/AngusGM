package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Authorization repository interface
 */
@NoRepositoryBean
public interface AuthorizationRepo extends BaseRepository<Authorization, Long> {

  /**
   * Count authorizations by status
   */
  long countByStatus(AuthorizationStatus status);

  /**
   * Count authorizations by subject type
   */
  long countBySubjectType(SubjectType subjectType);

  /**
   * Find authorizations by subject
   */
  List<Authorization> findBySubjectTypeAndSubjectId(SubjectType subjectType, Long subjectId);

  /**
   * Find authorizations by policy id
   */
  List<Authorization> findByPolicyId(Long policyId);

  /**
   * Count authorizations by policy id
   */
  long countByPolicyId(Long policyId);

  /**
   * Check if authorization exists for subject and policy
   */
  boolean existsBySubjectTypeAndSubjectIdAndPolicyId(SubjectType subjectType, Long subjectId, Long policyId);
}
