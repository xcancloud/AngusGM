package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserCreateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatsVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * User facade interface
 */
public interface UserFacade {

  /**
   * Create user
   */
  UserDetailVo create(UserCreateDto dto);

  /**
   * Update user
   */
  UserDetailVo update(Long id, UserUpdateDto dto);

  /**
   * Enable user
   */
  void enable(Long id);

  /**
   * Disable user
   */
  void disable(Long id);

  /**
   * Delete user
   */
  void delete(Long id);

  /**
   * Get user detail
   */
  UserDetailVo getDetail(Long id);

  /**
   * List users with pagination
   */
  PageResult<UserListVo> list(UserFindDto dto);

  /**
   * Get user statistics
   */
  UserStatsVo getStats();

  /**
   * Lock user
   */
  void lock(Long id);

  /**
   * Unlock user
   */
  void unlock(Long id);

  /**
   * Reset user password
   */
  void resetPassword(Long id, String newPassword);
}
