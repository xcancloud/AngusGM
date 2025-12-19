package cloud.xcan.angus.core.gm.interfaces.tenant;

import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantListVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tenant REST controller
 */
@Tag(name = "Tenant", description = "租户管理 - 租户的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantRest {

  @Resource
  private TenantFacade tenantFacade;

  @Operation(operationId = "getTenantsStats", summary = "获取租户统计数据", 
      description = "获取租户统计数据，包括总数、启用/禁用数量、增长率等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<TenantStatsVo> getStats() {
    return ApiLocaleResult.success(tenantFacade.getStats());
  }

  @Operation(operationId = "getTenantList", summary = "获取租户列表", 
      description = "获取租户列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "租户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<TenantListVo>> list(
      @Valid @ParameterObject TenantFindDto dto) {
    return ApiLocaleResult.success(tenantFacade.list(dto));
  }

  @Operation(operationId = "createTenant", summary = "创建租户", description = "创建新租户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "租户创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<TenantDetailVo> create(
      @Valid @RequestBody TenantCreateDto dto) {
    return ApiLocaleResult.success(tenantFacade.create(dto));
  }

  @Operation(operationId = "getTenantDetail", summary = "获取租户详情", 
      description = "获取指定租户的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "租户详情获取成功"),
      @ApiResponse(responseCode = "404", description = "租户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<TenantDetailVo> getDetail(
      @Parameter(description = "租户ID") @PathVariable Long id) {
    return ApiLocaleResult.success(tenantFacade.getDetail(id));
  }

  @Operation(operationId = "updateTenant", summary = "更新租户", 
      description = "更新租户基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<TenantDetailVo> update(
      @Parameter(description = "租户ID") @PathVariable Long id,
      @Valid @RequestBody TenantUpdateDto dto) {
    return ApiLocaleResult.success(tenantFacade.update(id, dto));
  }

  @Operation(operationId = "deleteTenant", summary = "删除租户", 
      description = "删除指定租户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "租户ID") @PathVariable Long id) {
    tenantFacade.delete(id);
  }

  @Operation(operationId = "enableTenant", summary = "启用租户", 
      description = "启用指定租户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/enable")
  public ApiLocaleResult<Void> enable(
      @Parameter(description = "租户ID") @PathVariable Long id) {
    tenantFacade.enable(id);
    return ApiLocaleResult.success(null);
  }

  @Operation(operationId = "disableTenant", summary = "禁用租户", 
      description = "禁用指定租户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "禁用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/disable")
  public ApiLocaleResult<Void> disable(
      @Parameter(description = "租户ID") @PathVariable Long id) {
    tenantFacade.disable(id);
    return ApiLocaleResult.success(null);
  }
}
