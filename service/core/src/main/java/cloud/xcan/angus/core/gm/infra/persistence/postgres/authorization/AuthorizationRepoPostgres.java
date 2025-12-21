package cloud.xcan.angus.core.gm.infra.persistence.postgres.authorization;

import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Authorization Repository PostgreSQL Implementation
 */
@Repository
@ConditionalOnProperty(name = "spring.datasource.platform", havingValue = "postgresql")
public interface AuthorizationRepoPostgres extends JpaRepository<Authorization, Long>, AuthorizationRepo {
    
    @Override
    Page<Authorization> findByStatus(AuthorizationStatus status, Pageable pageable);
    
    @Override
    Page<Authorization> findBySubjectType(SubjectType subjectType, Pageable pageable);
    
    @Override
    Page<Authorization> findBySubjectId(Long subjectId, Pageable pageable);
    
    @Override
    Page<Authorization> findByPolicyId(Long policyId, Pageable pageable);
    
    @Override
    long countByStatus(AuthorizationStatus status);
    
    @Override
    long countBySubjectType(SubjectType subjectType);
}
