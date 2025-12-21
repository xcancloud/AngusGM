package cloud.xcan.angus.core.gm.interfaces.service;

import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaTestDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceInstanceStatusDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaConfigVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaTestVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceHealthVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceInstanceStatusVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceRefreshVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service management REST controller
 */
@Tag(name = "Service", description = "服务管理 - 微服务实例管理、健康检查、服务注册中心配置")
@Validated
@RestController
@RequestMapping("/api/v1/services")
public class ServiceRest {

  @Resource
  private ServiceFacade serviceFacade;

  @Operation(operationId = "refreshServices", summary = "刷新服务列表", description = "从注册中心刷新服务列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "刷新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/refresh")
  public ApiLocaleResult<ServiceRefreshVo> refresh() {
    return ApiLocaleResult.success(serviceFacade.refresh());
  }

  @Operation(operationId = "updateInstanceStatus", summary = "下线/上线服务实例", description = "修改服务实例状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{serviceName}/instances/{instanceId}/status")
  public ApiLocaleResult<ServiceInstanceStatusVo> updateInstanceStatus(
      @Parameter(description = "服务名称") @PathVariable String serviceName,
      @Parameter(description = "实例ID") @PathVariable String instanceId,
      @Valid @RequestBody ServiceInstanceStatusDto dto) {
    return ApiLocaleResult.success(serviceFacade.updateInstanceStatus(serviceName, instanceId, dto));
  }

  @Operation(operationId = "getServiceDetail", summary = "获取服务详情", description = "获取指定服务的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{serviceName}")
  public ApiLocaleResult<ServiceDetailVo> getDetail(
      @Parameter(description = "服务名称") @PathVariable String serviceName) {
    return ApiLocaleResult.success(serviceFacade.getDetail(serviceName));
  }

  @Operation(operationId = "listServices", summary = "获取服务列表", description = "获取所有服务列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<ServiceListVo>> list(
      @ParameterObject ServiceFindDto dto) {
    return ApiLocaleResult.success(serviceFacade.list(dto));
  }

  @Operation(operationId = "getServiceStats", summary = "获取服务统计数据", description = "获取服务统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<ServiceStatsVo> getStats() {
    return ApiLocaleResult.success(serviceFacade.getStats());
  }

  @Operation(operationId = "getInstanceHealth", summary = "获取服务实例健康状态", description = "获取指定服务实例的健康状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{serviceName}/instances/{instanceId}/health")
  public ApiLocaleResult<ServiceHealthVo> getInstanceHealth(
      @Parameter(description = "服务名称") @PathVariable String serviceName,
      @Parameter(description = "实例ID") @PathVariable String instanceId) {
    return ApiLocaleResult.success(serviceFacade.getInstanceHealth(serviceName, instanceId));
  }

  @Operation(operationId = "getEurekaConfig", summary = "获取Eureka配置", description = "获取Eureka注册中心配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/eureka/config")
  public ApiLocaleResult<EurekaConfigVo> getEurekaConfig() {
    return ApiLocaleResult.success(serviceFacade.getEurekaConfig());
  }

  @Operation(operationId = "updateEurekaConfig", summary = "更新Eureka配置", description = "更新Eureka注册中心配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/eureka/config")
  public ApiLocaleResult<EurekaConfigVo> updateEurekaConfig(
      @Valid @RequestBody EurekaConfigUpdateDto dto) {
    return ApiLocaleResult.success(serviceFacade.updateEurekaConfig(dto));
  }

  @Operation(operationId = "testEurekaConnection", summary = "测试Eureka连接", description = "测试Eureka注册中心连接")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/eureka/test")
  public ApiLocaleResult<EurekaTestVo> testEurekaConnection(
      @Valid @RequestBody EurekaTestDto dto) {
    return ApiLocaleResult.success(serviceFacade.testEurekaConnection(dto));
  }

  @Operation(operationId = "getServiceCallStats", summary = "获取服务调用统计", description = "获取指定服务的调用统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{serviceName}/stats")
  public ApiLocaleResult<ServiceCallStatsVo> getServiceCallStats(
      @Parameter(description = "服务名称") @PathVariable String serviceName,
      @ParameterObject ServiceCallStatsDto dto) {
    return ApiLocaleResult.success(serviceFacade.getServiceCallStats(serviceName, dto));
  }
}
