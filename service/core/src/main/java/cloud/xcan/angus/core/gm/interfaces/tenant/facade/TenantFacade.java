package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantListVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * Tenant facade interface
 */
public interface TenantFacade {

  /**
   * Create tenant
   */
  TenantDetailVo create(TenantCreateDto dto);

  /**
   * Update tenant
   */
  TenantDetailVo update(Long id, TenantUpdateDto dto);

  /**
   * Enable tenant
   */
  void enable(Long id);

  /**
   * Disable tenant
   */
  void disable(Long id);

  /**
   * Delete tenant
   */
  void delete(Long id);

  /**
   * Get tenant detail
   */
  TenantDetailVo getDetail(Long id);

  /**
   * List tenants with pagination
   */
  PageResult<TenantListVo> list(TenantFindDto dto);

  /**
   * Get tenant statistics
   */
  TenantStatsVo getStats();
}
