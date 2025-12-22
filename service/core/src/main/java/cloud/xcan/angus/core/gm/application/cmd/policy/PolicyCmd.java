package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.domain.policy.Policy;
import java.util.List;

/**
 * Policy command service interface
 */
public interface PolicyCmd {

  /**
   * Create policy
   */
  Policy create(Policy policy);

  /**
   * Update policy
   */
  Policy update(Policy policy);

  /**
   * Delete policy
   */
  void delete(Long id);

  /**
   * Update policy permissions
   */
  Policy updatePermissions(Long id, List<Policy.PermissionInfo> permissions);

  /**
   * Set policy as default
   */
  Policy setDefault(Long id, Boolean isDefault);
}
