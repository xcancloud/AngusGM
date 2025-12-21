package cloud.xcan.angus.core.gm.interfaces.policy;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.PolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyDefaultDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyPermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AvailablePermissionVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyPermissionVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyStatsVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUserVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Role (Policy) REST controller
 */
@Tag(name = "Role", description = "角色管理 - 角色的创建、管理、权限配置等功能")
@Validated
@RestController
@RequestMapping("/api/v1/roles")
public class PolicyRest {

  @Resource
  private PolicyFacade policyFacade;

  // 创建
  @Operation(operationId = "createRole", summary = "创建角色", description = "创建新角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "角色创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<PolicyDetailVo> create(
      @Valid @RequestBody PolicyCreateDto dto) {
    return ApiLocaleResult.success(policyFacade.create(dto));
  }

  // 更新
  @Operation(operationId = "updateRole", summary = "更新角色", description = "更新角色基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<PolicyDetailVo> update(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @RequestBody PolicyUpdateDto dto) {
    return ApiLocaleResult.success(policyFacade.update(id, dto));
  }

  // 删除
  @Operation(operationId = "deleteRole", summary = "删除角色", description = "删除指定角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    policyFacade.delete(id);
  }

  // 查询详情
  @Operation(operationId = "getRoleDetail", summary = "获取角色详情", 
      description = "获取指定角色的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色详情获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<PolicyDetailVo> getDetail(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    return ApiLocaleResult.success(policyFacade.getDetail(id));
  }

  // 查询列表
  @Operation(operationId = "getRoleList", summary = "获取角色列表", 
      description = "获取角色列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<PolicyListVo>> list(
      @Valid @ParameterObject PolicyFindDto dto) {
    return ApiLocaleResult.success(policyFacade.list(dto));
  }

  // 查询统计
  @Operation(operationId = "getRoleStats", summary = "获取角色统计数据", 
      description = "获取角色统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<PolicyStatsVo> getStats() {
    return ApiLocaleResult.success(policyFacade.getStats());
  }

  // 获取角色权限配置
  @Operation(operationId = "getRolePermissions", summary = "获取角色权限配置", 
      description = "获取指定角色的权限配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "权限配置获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/permissions")
  public ApiLocaleResult<PolicyPermissionVo> getPermissions(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    return ApiLocaleResult.success(policyFacade.getPermissions(id));
  }

  // 更新角色权限
  @Operation(operationId = "updateRolePermissions", summary = "更新角色权限", 
      description = "更新指定角色的权限配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "权限更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/permissions")
  public ApiLocaleResult<PolicyPermissionVo> updatePermissions(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @RequestBody PolicyPermissionUpdateDto dto) {
    return ApiLocaleResult.success(policyFacade.updatePermissions(id, dto));
  }

  // 获取角色用户列表
  @Operation(operationId = "getRoleUsers", summary = "获取角色用户列表", 
      description = "获取指定角色下的用户列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/users")
  public ApiLocaleResult<PageResult<PolicyUserVo>> getUsers(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @ParameterObject PolicyUserFindDto dto) {
    return ApiLocaleResult.success(policyFacade.getUsers(id, dto));
  }

  // 设置默认角色
  @Operation(operationId = "setDefaultRole", summary = "设置默认角色", 
      description = "设置指定角色为默认角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "设置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/default")
  public ApiLocaleResult<PolicyDefaultVo> setDefault(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @RequestBody PolicyDefaultDto dto) {
    return ApiLocaleResult.success(policyFacade.setDefault(id, dto));
  }

  // 获取可用权限列表
  @Operation(operationId = "getAvailablePermissions", summary = "获取可用权限列表", 
      description = "获取指定应用的可用权限列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "权限列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/available-permissions")
  public ApiLocaleResult<List<AvailablePermissionVo>> getAvailablePermissions(
      @Parameter(description = "应用ID") @RequestParam(required = false) String appId) {
    return ApiLocaleResult.success(policyFacade.getAvailablePermissions(appId));
  }
}
