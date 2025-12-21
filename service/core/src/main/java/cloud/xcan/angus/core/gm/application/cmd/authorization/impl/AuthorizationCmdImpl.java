package cloud.xcan.angus.core.gm.application.cmd.authenticationorization.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authenticationorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.query.authenticationorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authorization Command Service Implementation
 */
@Biz
public class AuthorizationCmdImpl extends CommCmd<Authorization, Long> implements AuthorizationCmd {

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Authorization create(Authorization authorization) {
    return new BizTemplate<Authorization>() {
      @Override
      protected void checkParams() {
        // No specific validation needed for creation
      }

      @Override
      protected Authorization process() {
        // Set default status
        if (authorization.getStatus() == null) {
          authorization.setStatus(AuthorizationStatus.ENABLED);
        }

        insert(authorization);
        return authorization;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Authorization update(Authorization authorization) {
    return new BizTemplate<Authorization>() {
      Authorization authorizationDb;

      @Override
      protected void checkParams() {
        authorizationDb = authorizationQuery.findAndCheck(authorization.getId());
      }

      @Override
      protected Authorization process() {
        update(authorization, authorizationDb);
        return authorizationDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      Authorization authorizationDb;

      @Override
      protected void checkParams() {
        authorizationDb = authorizationQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        authorizationDb.setStatus(AuthorizationStatus.ENABLED);
        authorizationRepo.save(authorizationDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      Authorization authorizationDb;

      @Override
      protected void checkParams() {
        authorizationDb = authorizationQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        authorizationDb.setStatus(AuthorizationStatus.DISABLED);
        authorizationRepo.save(authorizationDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        authorizationQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        authorizationRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Authorization, Long> getRepository() {
    return authorizationRepo;
  }
}
