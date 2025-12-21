package cloud.xcan.angus.core.gm.interfaces.apimonitoring;

import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.ApiMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.SlowRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.TopRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.*;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口监控REST接口
 * 
 * API接口调用监控、性能分析、异常追踪
 */
@Tag(name = "InterfaceMonitoring", description = "接口监控 - API接口调用监控、性能分析、异常追踪")
@RestController
@RequestMapping("/api/v1/interface-monitoring")
@RequiredArgsConstructor
public class ApiMonitoringRest {
    
    private final ApiMonitoringFacade apiMonitoringFacade;
    
    // ==================== 监控概览 ====================
    
    @Operation(summary = "获取接口监控概览", description = "获取接口监控整体概览信息")
    @GetMapping("/overview")
    public ApiLocaleResult<InterfaceMonitoringOverviewVo> getOverview() {
        return ApiLocaleResult.success(apiMonitoringFacade.getOverview());
    }
    
    // ==================== 接口调用统计 ====================
    
    @Operation(summary = "获取接口调用统计列表", description = "分页获取接口调用统计列表")
    @GetMapping("/stats")
    public ApiLocaleResult<PageResult<InterfaceStatsVo>> listStats(@Valid InterfaceStatsFindDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.listStats(dto));
    }
    
    @Operation(summary = "获取单个接口详细统计", description = "获取指定接口的详细统计信息")
    @GetMapping("/stats/{serviceName}/{path}")
    public ApiLocaleResult<InterfaceStatsDetailVo> getStatsDetail(
            @Parameter(description = "服务名称") @PathVariable String serviceName,
            @Parameter(description = "接口路径（URL编码）") @PathVariable String path,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
            @Parameter(description = "时间周期（1h、6h、24h、7d、30d）") @RequestParam(required = false) String period) {
        return ApiLocaleResult.success(apiMonitoringFacade.getStatsDetail(serviceName, path, startDate, endDate, period));
    }
    
    // ==================== 慢请求分析 ====================
    
    @Operation(summary = "获取慢请求列表", description = "分页获取慢请求列表")
    @GetMapping("/slow-requests")
    public ApiLocaleResult<PageResult<SlowRequestVo>> listSlowRequests(@Valid SlowRequestFindDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.listSlowRequests(dto));
    }
    
    @Operation(summary = "获取慢请求详情", description = "获取指定慢请求记录的详细信息")
    @GetMapping("/slow-requests/{id}")
    public ApiLocaleResult<SlowRequestDetailVo> getSlowRequestDetail(
            @Parameter(description = "慢请求记录ID") @PathVariable String id) {
        return ApiLocaleResult.success(apiMonitoringFacade.getSlowRequestDetail(id));
    }
    
    // ==================== 错误请求分析 ====================
    
    @Operation(summary = "获取错误请求列表", description = "分页获取错误请求列表")
    @GetMapping("/error-requests")
    public ApiLocaleResult<PageResult<ErrorRequestVo>> listErrorRequests(@Valid ErrorRequestFindDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.listErrorRequests(dto));
    }
    
    @Operation(summary = "获取错误请求详情", description = "获取指定错误请求记录的详细信息")
    @GetMapping("/error-requests/{id}")
    public ApiLocaleResult<ErrorRequestDetailVo> getErrorRequestDetail(
            @Parameter(description = "错误请求记录ID") @PathVariable String id) {
        return ApiLocaleResult.success(apiMonitoringFacade.getErrorRequestDetail(id));
    }
    
    // ==================== 实时监控 ====================
    
    @Operation(summary = "获取实时QPS数据", description = "获取系统实时QPS数据")
    @GetMapping("/realtime/qps")
    public ApiLocaleResult<RealtimeQpsVo> getRealtimeQps() {
        return ApiLocaleResult.success(apiMonitoringFacade.getRealtimeQps());
    }
    
    @Operation(summary = "获取实时响应时间数据", description = "获取系统实时响应时间数据")
    @GetMapping("/realtime/response-time")
    public ApiLocaleResult<RealtimeResponseTimeVo> getRealtimeResponseTime() {
        return ApiLocaleResult.success(apiMonitoringFacade.getRealtimeResponseTime());
    }
    
    // ==================== TOP排行 ====================
    
    @Operation(summary = "获取调用量TOP接口", description = "获取调用量最高的接口列表")
    @GetMapping("/top/calls")
    public ApiLocaleResult<List<TopCallsVo>> getTopCalls(@Valid TopRequestFindDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.getTopCalls(dto));
    }
    
    @Operation(summary = "获取响应时间TOP接口", description = "获取响应时间最长的接口列表")
    @GetMapping("/top/slow")
    public ApiLocaleResult<List<TopSlowVo>> getTopSlow(@Valid TopRequestFindDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.getTopSlow(dto));
    }
    
    @Operation(summary = "获取错误率TOP接口", description = "获取错误率最高的接口列表")
    @GetMapping("/top/errors")
    public ApiLocaleResult<List<TopErrorsVo>> getTopErrors(@Valid TopRequestFindDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.getTopErrors(dto));
    }
}
