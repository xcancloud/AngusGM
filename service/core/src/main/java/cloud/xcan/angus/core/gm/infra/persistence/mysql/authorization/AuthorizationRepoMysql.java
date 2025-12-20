package cloud.xcan.angus.core.gm.infra.persistence.mysql.authorization;

import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Authorization Repository MySQL Implementation
 */
@Repository
@ConditionalOnProperty(name = "spring.datasource.platform", havingValue = "mysql", matchIfMissing = true)
public interface AuthorizationRepoMysql extends JpaRepository<Authorization, Long>, AuthorizationRepo {
    
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
