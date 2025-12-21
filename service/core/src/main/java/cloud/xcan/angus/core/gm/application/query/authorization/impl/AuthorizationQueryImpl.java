package cloud.xcan.angus.core.gm.application.query.authenticationorization.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.authenticationorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Authorization Query Service Implementation
 */
@Biz
@Service
public class AuthorizationQueryImpl implements AuthorizationQuery {

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Override
  public Authorization findAndCheck(Long id) {
    return new BizTemplate<Authorization>() {
      @Override
      protected Authorization process() {
        return authorizationRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("授权未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Authorization findById(Long id) {
    return authorizationRepo.findById(id).orElse(null);
  }

  @Override
  public Page<Authorization> find(GenericSpecification<Authorization> spec, PageRequest pageable,
                                  boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Authorization>>() {
      @Override
      protected Page<Authorization> process() {
        return authorizationRepo.findAll(spec, pageable);
      }
    }.execute();
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
