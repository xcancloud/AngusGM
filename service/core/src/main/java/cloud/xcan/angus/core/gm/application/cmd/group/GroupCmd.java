package cloud.xcan.angus.core.gm.application.cmd.group;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import java.util.List;

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
   * Update group status
   */
  void updateStatus(Long id, GroupStatus status);

  /**
   * Enable group
   */
  void enable(Long id);

  /**
   * Disable group
   */
  void disable(Long id);

  /**
   * Delete group
   */
  void delete(Long id);

  /**
   * Add members to group
   */
  void addMembers(Long groupId, List<Long> userIds);

  /**
   * Remove member from group
   */
  void removeMember(Long groupId, Long userId);

  /**
   * Remove members from group
   */
  void removeMembers(Long groupId, List<Long> userIds);

  /**
   * Update group owner
   */
  void updateOwner(Long groupId, Long ownerId);
}
