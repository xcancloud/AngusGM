package cloud.xcan.angus.core.gm.application.cmd.authorization.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.query.authenticationorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authenticationorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authenticationorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.AuthorizationStatus;
import cloud.xcan.angus.core.gm.domain.authenticationorization.enums.SubjectType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Authorization Command Service Implementation
 * </p>
 */
@Biz
public class AuthorizationCmdImpl extends CommCmd<Authorization, Long> implements AuthorizationCmd {

  private static final Logger log = LoggerFactory.getLogger(AuthorizationCmdImpl.class);

  private static final String ERROR_ROLE_IDS_EMPTY = "角色ID列表不能为空";
  private static final String ERROR_TARGET_TYPE_EMPTY = "目标类型不能为空";
  private static final String ERROR_TARGET_IDS_EMPTY = "目标ID列表不能为空";
  private static final String ERROR_ROLE_ID_EMPTY = "角色ID不能为空";
  private static final String ERROR_INVALID_TARGET_TYPE = "无效的目标类型: %s";
  private static final String ERROR_AUTHORIZATION_NOT_FOUND = "授权记录未找到";

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
  public void addRoles(Long authorizationId, List<Long> roleIds) {
    new BizTemplate<Void>() {
      Authorization authorization;
      
      @Override
      protected void checkParams() {
        authorization = authorizationQuery.findAndCheck(authorizationId);
        if (roleIds == null || roleIds.isEmpty()) {
          throw new IllegalArgumentException(ERROR_ROLE_IDS_EMPTY);
        }
        // TODO: Validate roleIds exist in role service
      }

      @Override
      protected Void process() {
        SubjectType subjectType = authorization.getSubjectType();
        Long subjectId = authorization.getSubjectId();
        
        for (Long roleId : roleIds) {
          if (!authorizationRepo.existsBySubjectTypeAndSubjectIdAndPolicyId(
              subjectType, subjectId, roleId)) {
            Authorization newAuthorization = createAuthorization(
                subjectType, subjectId, roleId, authorization);
            insert(newAuthorization);
          }
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeRole(Long authorizationId, Long roleId) {
    new BizTemplate<Void>() {
      Authorization authorization;
      
      @Override
      protected void checkParams() {
        authorization = authorizationQuery.findAndCheck(authorizationId);
        if (roleId == null) {
          throw new IllegalArgumentException(ERROR_ROLE_ID_EMPTY);
        }
      }

      @Override
      protected Void process() {
        if (authorization.getPolicyId().equals(roleId)) {
          authorizationRepo.deleteById(authorizationId);
        } else {
          deleteAuthorizationBySubjectAndRole(
              authorization.getSubjectType(), 
              authorization.getSubjectId(), 
              roleId);
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int batchCreate(String targetType, List<Long> targetIds, List<Long> roleIds) {
    return new BizTemplate<Integer>() {
      @Override
      protected void checkParams() {
        if (targetType == null || targetType.isEmpty()) {
          throw new IllegalArgumentException(ERROR_TARGET_TYPE_EMPTY);
        }
        if (targetIds == null || targetIds.isEmpty()) {
          throw new IllegalArgumentException(ERROR_TARGET_IDS_EMPTY);
        }
        if (roleIds == null || roleIds.isEmpty()) {
          throw new IllegalArgumentException(ERROR_ROLE_IDS_EMPTY);
        }
      }
      
      @Override
      protected Integer process() {
        SubjectType subjectType = parseSubjectType(targetType);
        int successCount = 0;
        
        for (Long targetId : targetIds) {
          try {
            for (Long roleId : roleIds) {
              if (!authorizationRepo.existsBySubjectTypeAndSubjectIdAndPolicyId(
                  subjectType, targetId, roleId)) {
                Authorization authorization = createAuthorization(
                    subjectType, targetId, roleId, null);
                insert(authorization);
                successCount++;
              }
            }
          } catch (Exception e) {
            log.error("Failed to create authorization for target: {}", targetId, e);
          }
        }
        
        return successCount;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchDelete(List<Long> authorizationIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (authorizationIds == null || authorizationIds.isEmpty()) {
          return;
        }
        
        try {
          authorizationRepo.deleteAllById(authorizationIds);
        } catch (Exception e) {
          log.error("Failed to batch delete authorizations", e);
          throw e;
        }
        
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

  /**
   * <p>
   * Create a new Authorization entity with the given parameters
   * </p>
   *
   * @param subjectType the subject type
   * @param subjectId the subject ID
   * @param roleId the role ID (policy ID)
   * @param template optional template authorization to copy validFrom, validTo, and description from
   * @return the created Authorization entity
   */
  private Authorization createAuthorization(
      SubjectType subjectType, 
      Long subjectId, 
      Long roleId, 
      Authorization template) {
    Authorization authorization = new Authorization();
    authorization.setSubjectType(subjectType);
    authorization.setSubjectId(subjectId);
    authorization.setPolicyId(roleId);
    authorization.setStatus(AuthorizationStatus.ENABLED);
    
    if (template != null) {
      authorization.setValidFrom(template.getValidFrom());
      authorization.setValidTo(template.getValidTo());
      authorization.setDescription(template.getDescription());
    }
    
    return authorization;
  }

  /**
   * <p>
   * Parse target type string to SubjectType enum
   * </p>
   *
   * @param targetType the target type string
   * @return the SubjectType enum
   * @throws IllegalArgumentException if the target type is invalid
   */
  private SubjectType parseSubjectType(String targetType) {
    try {
      return SubjectType.valueOf(targetType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(String.format(ERROR_INVALID_TARGET_TYPE, targetType));
    }
  }

  /**
   * <p>
   * Delete authorization by subject type, subject ID, and role ID
   * </p>
   *
   * @param subjectType the subject type
   * @param subjectId the subject ID
   * @param roleId the role ID (policy ID)
   * @throws ResourceNotFound if no authorization found
   */
  private void deleteAuthorizationBySubjectAndRole(
      SubjectType subjectType, 
      Long subjectId, 
      Long roleId) {
    if (!authorizationRepo.existsBySubjectTypeAndSubjectIdAndPolicyId(
        subjectType, subjectId, roleId)) {
      throw ResourceNotFound.of(ERROR_AUTHORIZATION_NOT_FOUND, new Object[]{});
    }
    
    Set<SearchCriteria> filters = new HashSet<>();
    filters.add(new SearchCriteria("subjectType", subjectType.name(), SearchOperation.EQUAL));
    filters.add(new SearchCriteria("subjectId", subjectId, SearchOperation.EQUAL));
    filters.add(new SearchCriteria("policyId", roleId, SearchOperation.EQUAL));
    GenericSpecification<Authorization> spec = new GenericSpecification<>(filters);
    
    List<Authorization> authorizations = authorizationRepo.findAll(spec);
    if (!authorizations.isEmpty()) {
      authorizationRepo.deleteAll(authorizations);
    }
  }
}
