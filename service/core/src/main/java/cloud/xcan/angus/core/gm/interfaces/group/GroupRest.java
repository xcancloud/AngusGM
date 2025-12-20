package cloud.xcan.angus.core.gm.interfaces.group;

import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
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
 * Group REST controller
 */
@Tag(name = "Group", description = "组管理 - 组的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/groups")
public class GroupRest {

  @Resource
  private GroupFacade groupFacade;

  // 创建
  @Operation(operationId = "createGroup", summary = "创建组", description = "创建新组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "组创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<GroupDetailVo> create(
      @Valid @RequestBody GroupCreateDto dto) {
    return ApiLocaleResult.success(groupFacade.create(dto));
  }

  // 更新
  @Operation(operationId = "updateGroup", summary = "更新组", description = "更新组基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<GroupDetailVo> update(
      @Parameter(description = "组ID") @PathVariable Long id,
      @Valid @RequestBody GroupUpdateDto dto) {
    return ApiLocaleResult.success(groupFacade.update(id, dto));
  }

  // 修改状态 - 启用
  @Operation(operationId = "enableGroup", summary = "启用组", description = "启用指定组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/enable")
  public ApiLocaleResult<Void> enable(
      @Parameter(description = "组ID") @PathVariable Long id) {
    groupFacade.enable(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 禁用
  @Operation(operationId = "disableGroup", summary = "禁用组", description = "禁用指定组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "禁用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/disable")
  public ApiLocaleResult<Void> disable(
      @Parameter(description = "组ID") @PathVariable Long id) {
    groupFacade.disable(id);
    return ApiLocaleResult.success(null);
  }

  // 删除
  @Operation(operationId = "deleteGroup", summary = "删除组", description = "删除指定组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "组ID") @PathVariable Long id) {
    groupFacade.delete(id);
  }

  // 查询详细
  @Operation(operationId = "getGroupDetail", summary = "获取组详情", 
      description = "获取指定组的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组详情获取成功"),
      @ApiResponse(responseCode = "404", description = "组不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<GroupDetailVo> getDetail(
      @Parameter(description = "组ID") @PathVariable Long id) {
    return ApiLocaleResult.success(groupFacade.getDetail(id));
  }

  // 查询列表
  @Operation(operationId = "getGroupList", summary = "获取组列表", 
      description = "获取组列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<GroupListVo>> list(
      @Valid @ParameterObject GroupFindDto dto) {
    return ApiLocaleResult.success(groupFacade.list(dto));
  }

  // 查询统计
  @Operation(operationId = "getGroupStats", summary = "获取组统计数据", 
      description = "获取组统计数据，包括总数、启用/禁用数量、类型分布等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<GroupStatsVo> getStats() {
    return ApiLocaleResult.success(groupFacade.getStats());
  }
}
