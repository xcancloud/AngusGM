package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.InviteStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
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
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserInviteResendVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserInviteVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserLockVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserResetPasswordVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatsVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatusUpdateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of user facade
 */
@Service
public class UserFacadeImpl implements UserFacade {

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  // ==================== 创建 ====================

  @Override
  public UserDetailVo create(UserCreateDto dto) {
    User user = UserAssembler.toCreateDomain(dto);
    User saved = userCmd.create(user);
    return UserAssembler.toDetailVo(saved);
  }

  // ==================== 更新 ====================

  @Override
  public UserDetailVo update(Long id, UserUpdateDto dto) {
    User user = UserAssembler.toUpdateDomain(id, dto);
    User saved = userCmd.update(user);
    return UserAssembler.toDetailVo(saved);
  }

  // ==================== 修改状态 ====================

  @Override
  public UserStatusUpdateVo updateStatus(Long id, UserStatusUpdateDto dto) {
    if (EnableStatus.ENABLED.equals(dto.getEnableStatus())) {
      userCmd.enable(id);
    } else {
      userCmd.disable(id);
    }
    UserStatusUpdateVo vo = new UserStatusUpdateVo();
    vo.setId(id);
    vo.setEnableStatus(dto.getEnableStatus());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public UserLockVo updateLock(Long id, UserLockDto dto) {
    if (Boolean.TRUE.equals(dto.getIsLocked())) {
      userCmd.lock(id);
    } else {
      userCmd.unlock(id);
    }
    UserLockVo vo = new UserLockVo();
    vo.setId(id);
    vo.setIsLocked(dto.getIsLocked());
    vo.setLockReason(dto.getReason());
    vo.setLockTime(LocalDateTime.now());
    return vo;
  }

  @Override
  public UserResetPasswordVo resetPassword(Long id, UserResetPasswordDto dto) {
    userCmd.resetPassword(id, dto.getNewPassword());
    UserResetPasswordVo vo = new UserResetPasswordVo();
    vo.setId(id);
    vo.setPasswordResetTime(LocalDateTime.now());
    vo.setEmailSent(dto.getSendEmail());
    // TODO: Send email notification if sendEmail is true
    return vo;
  }

  // ==================== 删除 ====================

  @Override
  public void delete(Long id) {
    userCmd.delete(id);
  }

  @Override
  public void batchDelete(UserBatchDeleteDto dto) {
    userCmd.batchDelete(new HashSet<>(dto.getUserIds()));
  }

  // ==================== 查询详细 ====================

  @Override
  public UserDetailVo getDetail(Long id) {
    User user = userQuery.findAndCheck(id);
    return UserAssembler.toDetailVo(user);
  }

  // ==================== 查询列表 ====================

  @Override
  public PageResult<UserListVo> list(UserFindDto dto) {
    GenericSpecification<User> spec = UserAssembler.getSpecification(dto);
    Page<User> page = userQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, UserAssembler::toListVo);
  }

  // ==================== 查询统计 ====================

  @Override
  public UserStatsVo getStats() {
    UserStatsVo stats = new UserStatsVo();
    
    // Count users by status - using Query instead of Repo
    long totalUsers = userQuery.count();
    long activeUsers = userQuery.countByStatus(UserStatus.ACTIVE);
    long disabledUsers = userQuery.countByEnableStatus(EnableStatus.DISABLED);
    long pendingUsers = userQuery.countByStatus(UserStatus.PENDING);
    
    stats.setTotalUsers(totalUsers);
    stats.setActiveUsers(activeUsers);
    stats.setDisabledUsers(disabledUsers);
    stats.setPendingUsers(pendingUsers);
    
    // TODO: Implement online users count and new users this month
    stats.setOnlineUsers(0L);
    stats.setNewUsersThisMonth(0L);
    
    return stats;
  }

  // ==================== 邀请相关 ====================

  @Override
  public UserInviteVo invite(UserInviteDto dto) {
    // TODO: Implement user invite logic
    UserInviteVo vo = new UserInviteVo();
    vo.setEmail(dto.getEmail());
    vo.setRoleId(dto.getRoleId());
    vo.setDepartmentId(dto.getDepartmentId());
    vo.setStatus(InviteStatus.PENDING);
    vo.setInviteDate(LocalDateTime.now());
    vo.setExpiryDate(LocalDateTime.now().plusDays(dto.getExpireDays()));
    return vo;
  }

  @Override
  public PageResult<UserInviteVo> listInvites(UserInviteFindDto dto) {
    // TODO: Implement invite list query
    return new PageResult<>(0L, new ArrayList<>());
  }

  @Override
  public void cancelInvite(Long id) {
    // TODO: Implement cancel invite logic
  }

  @Override
  public UserInviteResendVo resendInvite(Long id) {
    // TODO: Implement resend invite logic
    UserInviteResendVo vo = new UserInviteResendVo();
    vo.setId(id);
    vo.setResentTime(LocalDateTime.now());
    return vo;
  }

  // ==================== 当前用户相关 ====================

  @Override
  public void changePassword(UserChangePasswordDto dto) {
    // TODO: Implement change password logic with old password validation
  }

  @Override
  public UserDetailVo getCurrentUser() {
    // TODO: Get current user from security context
    return null;
  }

  @Override
  public UserDetailVo updateCurrentUser(UserCurrentUpdateDto dto) {
    // TODO: Update current user from security context
    return null;
  }
}
