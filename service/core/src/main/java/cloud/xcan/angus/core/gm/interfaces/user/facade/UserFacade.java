package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserChangePasswordDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserCreateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserInviteDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserInviteFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserLockDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserResetPasswordDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserInviteResendVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserInviteVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserLockVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserResetPasswordVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatsVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatusUpdateVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * User facade interface
 */
public interface UserFacade {

  // ==================== 创建 ====================

  /**
   * Create user
   */
  UserDetailVo create(UserCreateDto dto);

  // ==================== 更新 ====================

  /**
   * Update user
   */
  UserDetailVo update(Long id, UserUpdateDto dto);

  // ==================== 修改状态 ====================

  /**
   * Update user status (enable/disable)
   */
  UserStatusUpdateVo updateStatus(Long id, UserStatusUpdateDto dto);

  /**
   * Lock/unlock user
   */
  UserLockVo updateLock(Long id, UserLockDto dto);

  /**
   * Reset user password
   */
  UserResetPasswordVo resetPassword(Long id, UserResetPasswordDto dto);

  // ==================== 删除 ====================

  /**
   * Delete user
   */
  void delete(Long id);

  /**
   * Batch delete users
   */
  void batchDelete(UserBatchDeleteDto dto);

  // ==================== 查询详细 ====================

  /**
   * Get user detail
   */
  UserDetailVo getDetail(Long id);

  // ==================== 查询列表 ====================

  /**
   * List users with pagination
   */
  PageResult<UserListVo> list(UserFindDto dto);

  // ==================== 查询统计 ====================

  /**
   * Get user statistics
   */
  UserStatsVo getStats();

  // ==================== 邀请相关 ====================

  /**
   * Invite user
   */
  UserInviteVo invite(UserInviteDto dto);

  /**
   * Get invite list
   */
  PageResult<UserInviteVo> listInvites(UserInviteFindDto dto);

  /**
   * Cancel invite
   */
  void cancelInvite(Long id);

  /**
   * Resend invite
   */
  UserInviteResendVo resendInvite(Long id);

  // ==================== 当前用户相关 ====================

  /**
   * Change current user password
   */
  void changePassword(UserChangePasswordDto dto);

  /**
   * Get current user detail
   */
  UserDetailVo getCurrentUser();

  /**
   * Update current user
   */
  UserDetailVo updateCurrentUser(UserCurrentUpdateDto dto);
}
