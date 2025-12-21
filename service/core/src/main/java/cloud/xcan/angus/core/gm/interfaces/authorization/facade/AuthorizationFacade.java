package cloud.xcan.angus.core.gm.interfaces.authorization.facade;

import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationRoleAddVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationTargetVo;
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
   * Batch create authorizations
   */
  AuthorizationBatchVo batchCreate(AuthorizationBatchCreateDto dto);

  /**
   * Update authorization
   */
  AuthorizationDetailVo update(Long id, AuthorizationUpdateDto dto);

  /**
   * Add roles to authorization
   */
  AuthorizationRoleAddVo addRoles(Long id, AuthorizationRoleAddDto dto);

  /**
   * Remove role from authorization
   */
  void removeRole(Long id, Long roleId);

  /**
   * Delete authorization
   */
  void delete(Long id);

  /**
   * Batch delete authorizations
   */
  void batchDelete(AuthorizationBatchDeleteDto dto);

  /**
   * Get authorization detail
   */
  AuthorizationDetailVo getDetail(Long id);

  /**
   * Get target authorization info
   */
  AuthorizationTargetVo getTargetAuthorization(String targetType, String targetId);

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

  // TODO: 添加统计相关接口
}
