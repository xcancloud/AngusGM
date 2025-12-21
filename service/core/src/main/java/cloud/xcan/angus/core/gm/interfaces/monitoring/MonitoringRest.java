package cloud.xcan.angus.core.gm.interfaces.monitoring;

import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.MonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.AlertHandleDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.AlertRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.AlertRuleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.AlertRuleFindDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.ProcessFindDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.AlertHandleVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.AlertRecordVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.AlertRuleVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.DatabasePerformanceVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.DatabasePoolVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.ProcessInfoVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.RedisMonitorVo;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Monitoring", description = "系统监控 - 系统资源监控、性能指标、告警管理")
@Validated
@RestController
@RequestMapping("/api/v1/monitoring")
public class MonitoringRest {

  @Resource
  private MonitoringFacade monitoringFacade;

  @Operation(operationId = "createAlertRule", summary = "创建告警规则", description = "创建新的告警规则")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/alerts/rules")
  public ApiLocaleResult<AlertRuleVo> createAlertRule(
      @Valid @RequestBody AlertRuleCreateDto dto) {
    return ApiLocaleResult.success(monitoringFacade.createAlertRule(dto));
  }

  @Operation(operationId = "handleAlertRecord", summary = "处理告警记录", description = "处理告警记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "处理成功"),
      @ApiResponse(responseCode = "404", description = "告警记录不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/alerts/records/{id}/handle")
  public ApiLocaleResult<AlertHandleVo> handleAlertRecord(
      @Parameter(description = "告警记录ID") @PathVariable Long id,
      @Valid @RequestBody AlertHandleDto dto) {
    return ApiLocaleResult.success(monitoringFacade.handleAlertRecord(id, dto));
  }

  @Operation(operationId = "getMonitoringOverview", summary = "获取系统监控概览", description = "获取系统监控整体概览")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/overview")
  public ApiLocaleResult<MonitoringOverviewVo> getOverview() {
    return ApiLocaleResult.success(monitoringFacade.getOverview());
  }

  @Operation(operationId = "getSystemHealth", summary = "获取系统健康检查", description = "获取系统健康状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/health")
  public ApiLocaleResult<HealthCheckVo> getHealth() {
    return ApiLocaleResult.success(monitoringFacade.getHealth());
  }

  @Operation(operationId = "getCpuUsage", summary = "获取CPU使用率数据", description = "获取CPU使用率历史数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/cpu")
  public ApiLocaleResult<CpuUsageVo> getCpuUsage(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(monitoringFacade.getCpuUsage(period));
  }

  @Operation(operationId = "getMemoryUsage", summary = "获取内存使用数据", description = "获取内存使用历史数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/memory")
  public ApiLocaleResult<MemoryUsageVo> getMemoryUsage(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(monitoringFacade.getMemoryUsage(period));
  }

  @Operation(operationId = "getDiskUsage", summary = "获取磁盘使用数据", description = "获取磁盘使用信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/disk")
  public ApiLocaleResult<DiskUsageVo> getDiskUsage() {
    return ApiLocaleResult.success(monitoringFacade.getDiskUsage());
  }

  @Operation(operationId = "getNetworkUsage", summary = "获取网络流量数据", description = "获取网络流量历史数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/network")
  public ApiLocaleResult<NetworkUsageVo> getNetworkUsage(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(monitoringFacade.getNetworkUsage(period));
  }

  @Operation(operationId = "getProcesses", summary = "获取进程列表", description = "获取系统进程列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/processes")
  public ApiLocaleResult<PageResult<ProcessInfoVo>> getProcesses(
      @ParameterObject ProcessFindDto dto) {
    return ApiLocaleResult.success(monitoringFacade.getProcesses(dto));
  }

  @Operation(operationId = "getDatabasePools", summary = "获取数据库连接池状态", description = "获取数据库连接池状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/database/pools")
  public ApiLocaleResult<List<DatabasePoolVo>> getDatabasePools() {
    return ApiLocaleResult.success(monitoringFacade.getDatabasePools());
  }

  @Operation(operationId = "getDatabasePerformance", summary = "获取数据库性能指标", description = "获取数据库性能指标")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/database/performance")
  public ApiLocaleResult<DatabasePerformanceVo> getDatabasePerformance(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(monitoringFacade.getDatabasePerformance(period));
  }

  @Operation(operationId = "getRedisMonitor", summary = "获取Redis监控数据", description = "获取Redis缓存监控数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/cache/redis")
  public ApiLocaleResult<RedisMonitorVo> getRedisMonitor() {
    return ApiLocaleResult.success(monitoringFacade.getRedisMonitor());
  }

  @Operation(operationId = "getAlertRules", summary = "获取告警规则列表", description = "分页获取告警规则列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/alerts/rules")
  public ApiLocaleResult<PageResult<AlertRuleVo>> listAlertRules(
      @ParameterObject AlertRuleFindDto dto) {
    return ApiLocaleResult.success(monitoringFacade.listAlertRules(dto));
  }

  @Operation(operationId = "getAlertRecords", summary = "获取告警记录列表", description = "分页获取告警记录列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/alerts/records")
  public ApiLocaleResult<PageResult<AlertRecordVo>> listAlertRecords(
      @ParameterObject AlertRecordFindDto dto) {
    return ApiLocaleResult.success(monitoringFacade.listAlertRecords(dto));
  }
}
