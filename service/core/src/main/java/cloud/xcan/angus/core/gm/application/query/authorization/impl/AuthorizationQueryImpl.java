package cloud.xcan.angus.core.gm.application.query.authorization.impl;

import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.SubjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Authorization Query Service Implementation
 */
@Service
public class AuthorizationQueryImpl implements AuthorizationQuery {
    
    @Resource
    private AuthorizationRepo authorizationRepo;
    
    @Override
    public Authorization findById(Long id) {
        return authorizationRepo.findById(id).orElse(null);
    }
    
    @Override
    public Page<Authorization> findAll(Pageable pageable) {
        return authorizationRepo.findAll(pageable);
    }
    
    @Override
    public Page<Authorization> findByStatus(AuthorizationStatus status, Pageable pageable) {
        return authorizationRepo.findByStatus(status, pageable);
    }
    
    @Override
    public Page<Authorization> findBySubjectType(SubjectType subjectType, Pageable pageable) {
        return authorizationRepo.findBySubjectType(subjectType, pageable);
    }
    
    @Override
    public Page<Authorization> findBySubjectId(Long subjectId, Pageable pageable) {
        return authorizationRepo.findBySubjectId(subjectId, pageable);
    }
    
    @Override
    public Page<Authorization> findByPolicyId(Long policyId, Pageable pageable) {
        return authorizationRepo.findByPolicyId(policyId, pageable);
    }
    
    @Override
    public long count() {
        return authorizationRepo.count();
    }
    
    @Override
    public long countByStatus(AuthorizationStatus status) {
        return authorizationRepo.countByStatus(status);
    }
    
    @Override
    public long countBySubjectType(SubjectType subjectType) {
        return authorizationRepo.countBySubjectType(subjectType);
    }
}
