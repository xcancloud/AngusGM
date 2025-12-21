package cloud.xcan.angus.core.gm.interfaces.apimonitoring;

import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.ApiMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.SlowRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ErrorRequestDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ErrorRequestVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.InterfaceMonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.InterfaceStatsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.InterfaceStatsVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.RealtimeQpsVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.RealtimeResponseTimeVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.SlowRequestDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.SlowRequestVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.TopCallsVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.TopErrorsVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.TopSlowVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Interface Monitoring REST API
 */
@Tag(name = "InterfaceMonitoring", description = "接口监控 - API接口调用监控、性能分析、异常追踪")
@Validated
@RestController
@RequestMapping("/api/v1/interface-monitoring")
public class ApiMonitoringRest {

  @Resource
  private ApiMonitoringFacade apiMonitoringFacade;

  @Operation(operationId = "getInterfaceMonitoringOverview", summary = "获取接口监控概览", description = "获取接口监控整体概览信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/overview")
  public ApiLocaleResult<InterfaceMonitoringOverviewVo> getOverview() {
    return ApiLocaleResult.success(apiMonitoringFacade.getOverview());
  }

  @Operation(operationId = "listInterfaceStats", summary = "获取接口调用统计列表", description = "分页获取接口调用统计列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<PageResult<InterfaceStatsVo>> listStats(@Valid InterfaceStatsFindDto dto) {
    return ApiLocaleResult.success(apiMonitoringFacade.listStats(dto));
  }

  @Operation(operationId = "getInterfaceStatsDetail", summary = "获取单个接口详细统计", description = "获取指定接口的详细统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "接口不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats/{serviceName}/{path}")
  public ApiLocaleResult<InterfaceStatsDetailVo> getStatsDetail(
      @Parameter(description = "服务名称") @PathVariable String serviceName,
      @Parameter(description = "接口路径（URL编码）") @PathVariable String path,
      @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
      @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
      @Parameter(description = "时间周期（1h、6h、24h、7d、30d）") @RequestParam(required = false) String period) {
    return ApiLocaleResult.success(apiMonitoringFacade.getStatsDetail(serviceName, path, startDate, endDate, period));
  }

  @Operation(operationId = "listSlowRequests", summary = "获取慢请求列表", description = "分页获取慢请求列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/slow-requests")
  public ApiLocaleResult<PageResult<SlowRequestVo>> listSlowRequests(@Valid SlowRequestFindDto dto) {
    return ApiLocaleResult.success(apiMonitoringFacade.listSlowRequests(dto));
  }

  @Operation(operationId = "getSlowRequestDetail", summary = "获取慢请求详情", description = "获取指定慢请求记录的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "记录不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/slow-requests/{id}")
  public ApiLocaleResult<SlowRequestDetailVo> getSlowRequestDetail(
      @Parameter(description = "慢请求记录ID") @PathVariable Long id) {
    return ApiLocaleResult.success(apiMonitoringFacade.getSlowRequestDetail(id));
  }

  @Operation(operationId = "listErrorRequests", summary = "获取错误请求列表", description = "分页获取错误请求列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/error-requests")
  public ApiLocaleResult<PageResult<ErrorRequestVo>> listErrorRequests(@Valid ErrorRequestFindDto dto) {
    return ApiLocaleResult.success(apiMonitoringFacade.listErrorRequests(dto));
  }

  @Operation(operationId = "getErrorRequestDetail", summary = "获取错误请求详情", description = "获取指定错误请求记录的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "记录不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/error-requests/{id}")
  public ApiLocaleResult<ErrorRequestDetailVo> getErrorRequestDetail(
      @Parameter(description = "错误请求记录ID") @PathVariable Long id) {
    return ApiLocaleResult.success(apiMonitoringFacade.getErrorRequestDetail(id));
  }

  @Operation(operationId = "getRealtimeQps", summary = "获取实时QPS数据", description = "获取系统实时QPS数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/realtime/qps")
  public ApiLocaleResult<RealtimeQpsVo> getRealtimeQps() {
    return ApiLocaleResult.success(apiMonitoringFacade.getRealtimeQps());
  }

  @Operation(operationId = "getRealtimeResponseTime", summary = "获取实时响应时间数据", description = "获取系统实时响应时间数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/realtime/response-time")
  public ApiLocaleResult<RealtimeResponseTimeVo> getRealtimeResponseTime() {
    return ApiLocaleResult.success(apiMonitoringFacade.getRealtimeResponseTime());
  }

  @Operation(operationId = "getTopCalls", summary = "获取调用量TOP接口", description = "获取调用量最高的接口列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/top/calls")
  public ApiLocaleResult<List<TopCallsVo>> getTopCalls(
      @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "10") Integer limit,
      @Parameter(description = "时间周期（1h、6h、24h、7d、30d）") @RequestParam(required = false) String period) {
    return ApiLocaleResult.success(apiMonitoringFacade.getTopCalls(limit, period));
  }

  @Operation(operationId = "getTopSlow", summary = "获取响应时间TOP接口", description = "获取响应时间最长的接口列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/top/slow")
  public ApiLocaleResult<List<TopSlowVo>> getTopSlow(
      @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "10") Integer limit,
      @Parameter(description = "时间周期（1h、6h、24h、7d、30d）") @RequestParam(required = false) String period) {
    return ApiLocaleResult.success(apiMonitoringFacade.getTopSlow(limit, period));
  }

  @Operation(operationId = "getTopErrors", summary = "获取错误率TOP接口", description = "获取错误率最高的接口列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/top/errors")
  public ApiLocaleResult<List<TopErrorsVo>> getTopErrors(
      @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "10") Integer limit,
      @Parameter(description = "时间周期（1h、6h、24h、7d、30d）") @RequestParam(required = false) String period) {
    return ApiLocaleResult.success(apiMonitoringFacade.getTopErrors(limit, period));
  }
}
