package cloud.xcan.angus.core.gm.interfaces.department;

import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Department REST controller
 */
@Tag(name = "Department", description = "部门管理 - 部门的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentRest {

  @Resource
  private DepartmentFacade departmentFacade;

  // 创建
  @Operation(operationId = "createDepartment", summary = "创建部门", description = "创建新部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "部门创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<DepartmentDetailVo> create(
      @Valid @RequestBody DepartmentCreateDto dto) {
    return ApiLocaleResult.success(departmentFacade.create(dto));
  }

  // 更新
  @Operation(operationId = "updateDepartment", summary = "更新部门", 
      description = "更新部门基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<DepartmentDetailVo> update(
      @Parameter(description = "部门ID") @PathVariable Long id,
      @Valid @RequestBody DepartmentUpdateDto dto) {
    return ApiLocaleResult.success(departmentFacade.update(id, dto));
  }

  // 修改状态 - 启用
  @Operation(operationId = "enableDepartment", summary = "启用部门", 
      description = "启用指定部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/enable")
  public ApiLocaleResult<Void> enable(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    departmentFacade.enable(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 禁用
  @Operation(operationId = "disableDepartment", summary = "禁用部门", 
      description = "禁用指定部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "禁用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/disable")
  public ApiLocaleResult<Void> disable(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    departmentFacade.disable(id);
    return ApiLocaleResult.success(null);
  }

  // 删除
  @Operation(operationId = "deleteDepartment", summary = "删除部门", 
      description = "删除指定部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    departmentFacade.delete(id);
  }

  // 查询详细
  @Operation(operationId = "getDepartmentDetail", summary = "获取部门详情", 
      description = "获取指定部门的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门详情获取成功"),
      @ApiResponse(responseCode = "404", description = "部门不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<DepartmentDetailVo> getDetail(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    return ApiLocaleResult.success(departmentFacade.getDetail(id));
  }

  // 查询列表
  @Operation(operationId = "getDepartmentList", summary = "获取部门列表", 
      description = "获取部门列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<DepartmentListVo>> list(
      @Valid @ParameterObject DepartmentFindDto dto) {
    return ApiLocaleResult.success(departmentFacade.list(dto));
  }

  // 查询统计
  @Operation(operationId = "getDepartmentStats", summary = "获取部门统计数据", 
      description = "获取部门统计数据，包括总数、启用/禁用数量等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<DepartmentStatsVo> getStats() {
    return ApiLocaleResult.success(departmentFacade.getStats());
  }

  // 获取部门树
  @Operation(operationId = "getDepartmentTree", summary = "获取部门树", 
      description = "获取部门树形结构")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门树获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/tree")
  public ApiLocaleResult<List<DepartmentDetailVo>> getTree(
      @Parameter(description = "父部门ID，为空则获取根节点") @RequestParam(required = false) Long parentId) {
    return ApiLocaleResult.success(departmentFacade.getTree(parentId));
  }
}
