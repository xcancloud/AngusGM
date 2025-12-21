package cloud.xcan.angus.core.gm.interfaces.monitoring;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.MonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Monitoring", description = "系统监控 - 系统资源监控、性能指标、告警管理")
@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
public class MonitoringRest {

    private final MonitoringFacade monitoringFacade;

    // ==================== 系统概览 ====================

    @Operation(summary = "获取系统监控概览", description = "获取系统监控整体概览")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/overview")
    public ApiLocaleResult<MonitoringOverviewVo> getOverview() {
        return ApiLocaleResult.success(monitoringFacade.getOverview());
    }

    @Operation(summary = "获取系统健康检查", description = "获取系统健康状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/health")
    public ApiLocaleResult<HealthCheckVo> getHealth() {
        return ApiLocaleResult.success(monitoringFacade.getHealth());
    }

    // ==================== 资源监控 ====================

    @Operation(summary = "获取CPU使用率数据", description = "获取CPU使用率历史数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/cpu")
    public ApiLocaleResult<CpuUsageVo> getCpuUsage(@RequestParam(defaultValue = "1h") String period) {
        return ApiLocaleResult.success(monitoringFacade.getCpuUsage(period));
    }

    @Operation(summary = "获取内存使用数据", description = "获取内存使用历史数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/memory")
    public ApiLocaleResult<MemoryUsageVo> getMemoryUsage(@RequestParam(defaultValue = "1h") String period) {
        return ApiLocaleResult.success(monitoringFacade.getMemoryUsage(period));
    }

    @Operation(summary = "获取磁盘使用数据", description = "获取磁盘使用信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/disk")
    public ApiLocaleResult<DiskUsageVo> getDiskUsage() {
        return ApiLocaleResult.success(monitoringFacade.getDiskUsage());
    }

    @Operation(summary = "获取网络流量数据", description = "获取网络流量历史数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/network")
    public ApiLocaleResult<NetworkUsageVo> getNetworkUsage(@RequestParam(defaultValue = "1h") String period) {
        return ApiLocaleResult.success(monitoringFacade.getNetworkUsage(period));
    }

    @Operation(summary = "获取进程列表", description = "获取系统进程列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/processes")
    public ApiLocaleResult<PageResult<ProcessInfoVo>> getProcesses(@ParameterObject ProcessFindDto dto) {
        return ApiLocaleResult.success(monitoringFacade.getProcesses(dto));
    }

    // ==================== 数据库监控 ====================

    @Operation(summary = "获取数据库连接池状态", description = "获取数据库连接池状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/database/pools")
    public ApiLocaleResult<List<DatabasePoolVo>> getDatabasePools() {
        return ApiLocaleResult.success(monitoringFacade.getDatabasePools());
    }

    @Operation(summary = "获取数据库性能指标", description = "获取数据库性能指标")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/database/performance")
    public ApiLocaleResult<DatabasePerformanceVo> getDatabasePerformance(@RequestParam(defaultValue = "1h") String period) {
        return ApiLocaleResult.success(monitoringFacade.getDatabasePerformance(period));
    }

    // ==================== 缓存监控 ====================

    @Operation(summary = "获取Redis监控数据", description = "获取Redis缓存监控数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/cache/redis")
    public ApiLocaleResult<RedisMonitorVo> getRedisMonitor() {
        return ApiLocaleResult.success(monitoringFacade.getRedisMonitor());
    }

    // ==================== 告警规则 ====================

    @Operation(summary = "获取告警规则列表", description = "分页获取告警规则列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/alerts/rules")
    public ApiLocaleResult<PageResult<AlertRuleVo>> listAlertRules(@ParameterObject AlertRuleFindDto dto) {
        return ApiLocaleResult.success(monitoringFacade.listAlertRules(dto));
    }

    @Operation(summary = "创建告警规则", description = "创建新的告警规则")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/alerts/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<AlertRuleVo> createAlertRule(@Valid @RequestBody AlertRuleCreateDto dto) {
        return ApiLocaleResult.success(monitoringFacade.createAlertRule(dto));
    }

    // ==================== 告警记录 ====================

    @Operation(summary = "获取告警记录列表", description = "分页获取告警记录列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/alerts/records")
    public ApiLocaleResult<PageResult<AlertRecordVo>> listAlertRecords(@ParameterObject AlertRecordFindDto dto) {
        return ApiLocaleResult.success(monitoringFacade.listAlertRecords(dto));
    }

    @Operation(summary = "处理告警记录", description = "处理告警记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "处理成功"),
            @ApiResponse(responseCode = "404", description = "告警记录不存在")
    })
    @PatchMapping("/alerts/records/{id}/handle")
    public ApiLocaleResult<AlertHandleVo> handleAlertRecord(
            @Parameter(description = "告警记录ID") @PathVariable Long id,
            @Valid @RequestBody AlertHandleDto dto) {
        return ApiLocaleResult.success(monitoringFacade.handleAlertRecord(id, dto));
    }
}
