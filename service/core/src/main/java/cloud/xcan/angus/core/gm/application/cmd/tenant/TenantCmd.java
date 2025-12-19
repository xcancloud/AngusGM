package cloud.xcan.angus.core.gm.application.cmd.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.Tenant;

/**
 * Tenant command service interface
 */
public interface TenantCmd {

  /**
   * Create tenant
   */
  Tenant create(Tenant tenant);

  /**
   * Update tenant
   */
  Tenant update(Tenant tenant);

  /**
   * Delete tenant
   */
  void delete(Long id);

  /**
   * Enable tenant
   */
  void enable(Long id);

  /**
   * Disable tenant
   */
  void disable(Long id);
}
