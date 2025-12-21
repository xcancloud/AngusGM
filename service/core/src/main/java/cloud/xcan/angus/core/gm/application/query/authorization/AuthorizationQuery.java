package cloud.xcan.angus.core.gm.application.query.authenticationorization;

import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Authorization Query Service
 * Handles read operations for authorization management
 */
public interface AuthorizationQuery {

  /**
   * Find authorization by ID and check existence
   */
  Authorization findAndCheck(Long id);

  /**
   * Find authorization by ID
   */
  Authorization findById(Long id);

  /**
   * Find authorizations with pagination and specification
   */
  Page<Authorization> find(GenericSpecification<Authorization> spec, PageRequest pageable,
                           boolean fullTextSearch, String[] match);

  /**
   * Find all authorizations with pagination
   */
  Page<Authorization> findAll(Pageable pageable);

  /**
   * Find authorizations by status
   */
  Page<Authorization> findByStatus(AuthorizationStatus status, Pageable pageable);

  /**
   * Find authorizations by subject type
   */
  Page<Authorization> findBySubjectType(SubjectType subjectType, Pageable pageable);

  /**
   * Find authorizations by subject ID
   */
  Page<Authorization> findBySubjectId(Long subjectId, Pageable pageable);

  /**
   * Find authorizations by policy ID
   */
  Page<Authorization> findByPolicyId(Long policyId, Pageable pageable);

  /**
   * Count total authorizations
   */
  long count();

  /**
   * Count authorizations by status
   */
  long countByStatus(AuthorizationStatus status);

  /**
   * Count authorizations by subject type
   */
  long countBySubjectType(SubjectType subjectType);
}
