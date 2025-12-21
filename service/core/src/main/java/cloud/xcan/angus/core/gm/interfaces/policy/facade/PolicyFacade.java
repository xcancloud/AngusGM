package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyDefaultDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyPermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AvailablePermissionVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyPermissionVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyStatsVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUserVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

/**
 * Policy (Role) facade interface
 */
public interface PolicyFacade {

  /**
   * Create role
   */
  PolicyDetailVo create(PolicyCreateDto dto);

  /**
   * Update role
   */
  PolicyDetailVo update(Long id, PolicyUpdateDto dto);

  /**
   * Delete role
   */
  void delete(Long id);

  /**
   * Get role detail
   */
  PolicyDetailVo getDetail(Long id);

  /**
   * List roles with pagination
   */
  PageResult<PolicyListVo> list(PolicyFindDto dto);

  /**
   * Get role statistics
   */
  PolicyStatsVo getStats();

  /**
   * Get role permissions
   */
  PolicyPermissionVo getPermissions(Long id);

  /**
   * Update role permissions
   */
  PolicyPermissionVo updatePermissions(Long id, PolicyPermissionUpdateDto dto);

  /**
   * Get users of a role
   */
  PageResult<PolicyUserVo> getUsers(Long id, PolicyUserFindDto dto);

  /**
   * Set role as default
   */
  PolicyDefaultVo setDefault(Long id, PolicyDefaultDto dto);

  /**
   * Get available permissions for an application
   */
  List<AvailablePermissionVo> getAvailablePermissions(String appId);
}
