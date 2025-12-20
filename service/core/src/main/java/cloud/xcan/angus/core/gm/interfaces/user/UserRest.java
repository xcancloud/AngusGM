package cloud.xcan.angus.core.gm.interfaces.user;

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
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserInviteResendVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserInviteVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserLockVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserResetPasswordVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatsVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatusUpdateVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * User REST controller
 */
@Tag(name = "User", description = "用户管理 - 用户的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserRest {

  @Resource
  private UserFacade userFacade;

  // ==================== 创建 ====================

  @Operation(operationId = "createUser", summary = "创建用户", description = "创建新用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "用户创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<UserDetailVo> create(
      @Valid @RequestBody UserCreateDto dto) {
    return ApiLocaleResult.success(userFacade.create(dto));
  }

  // ==================== 更新 ====================

  @Operation(operationId = "updateUser", summary = "更新用户", description = "更新用户基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<UserDetailVo> update(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserUpdateDto dto) {
    return ApiLocaleResult.success(userFacade.update(id, dto));
  }

  // ==================== 修改状态 ====================

  @Operation(operationId = "updateUserStatus", summary = "启用/禁用用户", description = "更新用户启用状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/status")
  public ApiLocaleResult<UserStatusUpdateVo> updateStatus(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserStatusUpdateDto dto) {
    return ApiLocaleResult.success(userFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "updateUserLock", summary = "锁定/解锁用户", description = "锁定或解锁指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "操作成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/lock")
  public ApiLocaleResult<UserLockVo> updateLock(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserLockDto dto) {
    return ApiLocaleResult.success(userFacade.updateLock(id, dto));
  }

  @Operation(operationId = "resetUserPassword", summary = "重置密码", description = "重置用户密码")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码重置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/reset-password")
  public ApiLocaleResult<UserResetPasswordVo> resetPassword(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserResetPasswordDto dto) {
    return ApiLocaleResult.success(userFacade.resetPassword(id, dto));
  }

  // ==================== 删除 ====================

  @Operation(operationId = "deleteUser", summary = "删除用户", description = "删除指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    userFacade.delete(id);
  }

  @Operation(operationId = "batchDeleteUsers", summary = "批量删除用户", description = "批量删除指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/batch")
  public void batchDelete(@Valid @RequestBody UserBatchDeleteDto dto) {
    userFacade.batchDelete(dto);
  }

  // ==================== 查询详细 ====================

  @Operation(operationId = "getUserDetail", summary = "获取用户详情", 
      description = "获取指定用户的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户详情获取成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<UserDetailVo> getDetail(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    return ApiLocaleResult.success(userFacade.getDetail(id));
  }

  // ==================== 查询列表 ====================

  @Operation(operationId = "getUserList", summary = "获取用户列表", 
      description = "获取用户列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<UserListVo>> list(
      @Valid @ParameterObject UserFindDto dto) {
    return ApiLocaleResult.success(userFacade.list(dto));
  }

  // ==================== 查询统计 ====================

  @Operation(operationId = "getUserStats", summary = "获取用户统计数据", 
      description = "获取用户统计数据，包括总数、激活/禁用数量等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<UserStatsVo> getStats() {
    return ApiLocaleResult.success(userFacade.getStats());
  }

  // ==================== 邀请相关接口 ====================

  @Operation(operationId = "inviteUser", summary = "邀请用户", description = "发送用户邀请")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "邀请发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invite")
  public ApiLocaleResult<UserInviteVo> invite(@Valid @RequestBody UserInviteDto dto) {
    return ApiLocaleResult.success(userFacade.invite(dto));
  }

  @Operation(operationId = "getInviteList", summary = "获取邀请列表", description = "获取用户邀请列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "邀请列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/invites")
  public ApiLocaleResult<PageResult<UserInviteVo>> listInvites(
      @Valid @ParameterObject UserInviteFindDto dto) {
    return ApiLocaleResult.success(userFacade.listInvites(dto));
  }

  @Operation(operationId = "cancelInvite", summary = "取消邀请", description = "取消指定邀请")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "取消成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/invites/{id}")
  public void cancelInvite(@Parameter(description = "邀请ID") @PathVariable Long id) {
    userFacade.cancelInvite(id);
  }

  @Operation(operationId = "resendInvite", summary = "重新发送邀请", description = "重新发送指定邀请")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "邀请已重新发送")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/invites/{id}/resend")
  public ApiLocaleResult<UserInviteResendVo> resendInvite(
      @Parameter(description = "邀请ID") @PathVariable Long id) {
    return ApiLocaleResult.success(userFacade.resendInvite(id));
  }

  // ==================== 当前用户相关接口 ====================

  @Operation(operationId = "changePassword", summary = "修改密码", description = "当前用户修改密码")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码修改成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/change-password")
  public ApiLocaleResult<Void> changePassword(@Valid @RequestBody UserChangePasswordDto dto) {
    userFacade.changePassword(dto);
    return ApiLocaleResult.success(null);
  }

  @Operation(operationId = "getCurrentUser", summary = "获取当前用户信息", 
      description = "获取当前登录用户的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户信息获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/current")
  public ApiLocaleResult<UserDetailVo> getCurrentUser() {
    return ApiLocaleResult.success(userFacade.getCurrentUser());
  }

  @Operation(operationId = "updateCurrentUser", summary = "更新当前用户信息", 
      description = "更新当前登录用户的信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/current")
  public ApiLocaleResult<UserDetailVo> updateCurrentUser(
      @Valid @RequestBody UserCurrentUpdateDto dto) {
    return ApiLocaleResult.success(userFacade.updateCurrentUser(dto));
  }
}
