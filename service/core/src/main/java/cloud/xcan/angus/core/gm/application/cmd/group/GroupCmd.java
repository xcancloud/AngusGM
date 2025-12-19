package cloud.xcan.angus.core.gm.application.cmd.group;

import cloud.xcan.angus.core.gm.domain.group.Group;

/**
 * Group command service interface
 */
public interface GroupCmd {

  /**
   * Create group
   */
  Group create(Group group);

  /**
   * Update group
   */
  Group update(Group group);

  /**
   * Delete group
   */
  void delete(Long id);

  /**
   * Enable group
   */
  void enable(Long id);

  /**
   * Disable group
   */
  void disable(Long id);
}
