package cloud.xcan.angus.core.gm.interfaces.interface_;

import cloud.xcan.angus.core.gm.interfaces.interface_.facade.InterfaceFacade;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceDeprecateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceSyncDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceDeprecateVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceServiceVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceSyncVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceTagVo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Interface management REST controller
 */
@Tag(name = "Interface", description = "接口管理 - API接口文档管理、接口同步、接口查询")
@Validated
@RestController
@RequestMapping("/api/v1/interfaces")
public class InterfaceRest {

  @Resource
  private InterfaceFacade interfaceFacade;

  // ================================
  // 同步操作
  // ================================

  @Operation(operationId = "syncInterfaces", summary = "同步服务接口", description = "同步指定服务的接口文档")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "同步成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/sync")
  public ApiLocaleResult<InterfaceSyncVo> sync(
      @Valid @RequestBody InterfaceSyncDto dto) {
    return ApiLocaleResult.success(interfaceFacade.sync(dto));
  }

  @Operation(operationId = "syncAllInterfaces", summary = "批量同步所有服务接口", description = "同步所有服务的接口文档")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "批量同步成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/sync-all")
  public ApiLocaleResult<InterfaceSyncVo> syncAll() {
    return ApiLocaleResult.success(interfaceFacade.syncAll());
  }

  // ================================
  // 状态操作
  // ================================

  @Operation(operationId = "deprecateInterface", summary = "标记接口为废弃", description = "标记指定接口为废弃状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标记成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/deprecate")
  public ApiLocaleResult<InterfaceDeprecateVo> deprecate(
      @Parameter(description = "接口ID") @PathVariable String id,
      @Valid @RequestBody InterfaceDeprecateDto dto) {
    return ApiLocaleResult.success(interfaceFacade.deprecate(id, dto));
  }

  // ================================
  // 查询接口
  // ================================

  @Operation(operationId = "getInterfaceDetail", summary = "获取接口详情", description = "获取指定接口的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<InterfaceDetailVo> getDetail(
      @Parameter(description = "接口ID") @PathVariable String id) {
    return ApiLocaleResult.success(interfaceFacade.getDetail(id));
  }

  @Operation(operationId = "listInterfaces", summary = "获取接口列表", description = "分页查询接口列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<InterfaceListVo>> list(
      @ParameterObject InterfaceFindDto dto) {
    return ApiLocaleResult.success(interfaceFacade.list(dto));
  }

  @Operation(operationId = "listInterfacesByService", summary = "获取服务的接口列表", description = "获取指定服务的接口列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/service/{serviceName}")
  public ApiLocaleResult<PageResult<InterfaceListVo>> listByService(
      @Parameter(description = "服务名称") @PathVariable String serviceName,
      @ParameterObject InterfaceFindDto dto) {
    return ApiLocaleResult.success(interfaceFacade.listByService(serviceName, dto));
  }

  @Operation(operationId = "listInterfacesByTag", summary = "按标签获取接口列表", description = "获取指定标签的接口列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/tag/{tag}")
  public ApiLocaleResult<PageResult<InterfaceListVo>> listByTag(
      @Parameter(description = "标签名称") @PathVariable String tag,
      @ParameterObject InterfaceFindDto dto) {
    return ApiLocaleResult.success(interfaceFacade.listByTag(tag, dto));
  }

  // ================================
  // 服务和标签查询
  // ================================

  @Operation(operationId = "getInterfaceServices", summary = "获取所有服务列表", description = "获取所有服务列表（带接口数量）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/services")
  public ApiLocaleResult<List<InterfaceServiceVo>> getServices() {
    return ApiLocaleResult.success(interfaceFacade.getServices());
  }

  @Operation(operationId = "getInterfaceTags", summary = "获取所有标签列表", description = "获取所有标签列表（带接口数量）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/tags")
  public ApiLocaleResult<List<InterfaceTagVo>> getTags() {
    return ApiLocaleResult.success(interfaceFacade.getTags());
  }

  // ================================
  // 统计查询
  // ================================

  @Operation(operationId = "getInterfaceCallStats", summary = "获取接口调用统计", description = "获取指定接口的调用统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/stats")
  public ApiLocaleResult<InterfaceCallStatsVo> getCallStats(
      @Parameter(description = "接口ID") @PathVariable String id,
      @ParameterObject InterfaceCallStatsDto dto) {
    return ApiLocaleResult.success(interfaceFacade.getCallStats(id, dto));
  }
}
