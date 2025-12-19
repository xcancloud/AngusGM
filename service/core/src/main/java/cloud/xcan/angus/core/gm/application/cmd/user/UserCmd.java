package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.core.gm.domain.user.User;

/**
 * User command service interface
 */
public interface UserCmd {

  /**
   * Create user
   */
  User create(User user);

  /**
   * Update user
   */
  User update(User user);

  /**
   * Delete user
   */
  void delete(Long id);

  /**
   * Enable user
   */
  void enable(Long id);

  /**
   * Disable user
   */
  void disable(Long id);

  /**
   * Lock user
   */
  void lock(Long id);

  /**
   * Unlock user
   */
  void unlock(Long id);

  /**
   * Reset password
   */
  void resetPassword(Long id, String newPassword);
}
