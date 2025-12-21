package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade;

import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationRoleAddVo;
import cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.vo.AuthorizationTargetVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * Authorization Facade
 */
public interface AuthorizationFacade {

  /**
   * Create authorization
   */
  AuthorizationDetailVo create(AuthorizationCreateDto dto);

  /**
   * Update authorization
   */
  AuthorizationDetailVo update(Long id, AuthorizationUpdateDto dto);

  /**
   * Delete authorization
   */
  void delete(Long id);

  /**
   * Get authorization detail
   */
  AuthorizationDetailVo getDetail(Long id);

  /**
   * List authorizations with pagination
   */
  PageResult<AuthorizationListVo> list(AuthorizationFindDto dto);

  /**
   * List user authorizations
   */
  PageResult<AuthorizationListVo> listUsers(AuthorizationFindDto dto);

  /**
   * List department authorizations
   */
  PageResult<AuthorizationListVo> listDepartments(AuthorizationFindDto dto);

  /**
   * List group authorizations
   */
  PageResult<AuthorizationListVo> listGroups(AuthorizationFindDto dto);

  /**
   * Batch create authorizations
   */
  AuthorizationBatchVo batchCreate(AuthorizationBatchCreateDto dto);

  /**
   * Batch delete authorizations
   */
  void batchDelete(AuthorizationBatchDeleteDto dto);

  /**
   * Get target authorization info
   */
  AuthorizationTargetVo getTargetAuthorization(String targetType, String targetId);

  /**
   * Add roles to authorization
   */
  AuthorizationRoleAddVo addRoles(Long id, AuthorizationRoleAddDto dto);

  /**
   * Remove role from authorization
   */
  void removeRole(Long id, Long roleId);
}
