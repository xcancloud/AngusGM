package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserCreateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserStatsVo;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  // 创建
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

  // 更新
  @Operation(operationId = "updateUser", summary = "更新用户", description = "更新用户基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<UserDetailVo> update(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserUpdateDto dto) {
    return ApiLocaleResult.success(userFacade.update(id, dto));
  }

  // 修改状态 - 启用
  @Operation(operationId = "enableUser", summary = "启用用户", description = "启用指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/enable")
  public ApiLocaleResult<Void> enable(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    userFacade.enable(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 禁用
  @Operation(operationId = "disableUser", summary = "禁用用户", description = "禁用指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "禁用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/disable")
  public ApiLocaleResult<Void> disable(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    userFacade.disable(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 锁定
  @Operation(operationId = "lockUser", summary = "锁定用户", description = "锁定指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "锁定成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/lock")
  public ApiLocaleResult<Void> lock(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    userFacade.lock(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 解锁
  @Operation(operationId = "unlockUser", summary = "解锁用户", description = "解锁指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "解锁成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/unlock")
  public ApiLocaleResult<Void> unlock(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    userFacade.unlock(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 重置密码
  @Operation(operationId = "resetUserPassword", summary = "重置密码", description = "重置用户密码")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码重置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/reset-password")
  public ApiLocaleResult<Void> resetPassword(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Parameter(description = "新密码") @RequestParam String newPassword) {
    userFacade.resetPassword(id, newPassword);
    return ApiLocaleResult.success(null);
  }

  // 删除
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

  // 查询详细
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

  // 查询列表
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

  // 查询统计
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
}
