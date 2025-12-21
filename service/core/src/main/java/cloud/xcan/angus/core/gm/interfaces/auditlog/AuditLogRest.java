package cloud.xcan.angus.core.gm.interfaces.auditlog;

import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.AuditLogFacade;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogCleanupDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogExportDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFailureFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogSensitiveFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUserHistoryDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.RetentionPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogExportResultVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogExportStatusVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.ModuleStatsVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.RetentionPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.SensitiveLogVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Audit Log REST API
 */
@Tag(name = "AuditLogs", description = "审计日志 - 系统操作日志记录、查询、导出")
@Validated
@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogRest {

  @Resource
  private AuditLogFacade auditLogFacade;

  @Operation(operationId = "updateAuditLogRetentionPolicy", summary = "更新审计日志保留策略", description = "更新审计日志保留策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/retention-policy")
  public ApiLocaleResult<RetentionPolicyVo> updateRetentionPolicy(@Valid @RequestBody RetentionPolicyUpdateDto dto) {
    return ApiLocaleResult.success(auditLogFacade.updateRetentionPolicy(dto));
  }

  @Operation(operationId = "exportAuditLogs", summary = "导出审计日志", description = "创建审计日志导出任务")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "导出任务已创建"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/export")
  public ApiLocaleResult<AuditLogExportResultVo> export(@Valid @RequestBody AuditLogExportDto dto) {
    return ApiLocaleResult.success(auditLogFacade.export(dto));
  }

  @Operation(operationId = "cleanupAuditLogs", summary = "清理审计日志", description = "清理指定日期之前的审计日志")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "清理完成"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/cleanup")
  public void cleanup(@Valid @RequestBody AuditLogCleanupDto dto) {
    auditLogFacade.cleanup(dto);
  }

  @Operation(operationId = "getAuditLogDetail", summary = "获取审计日志详情", description = "获取指定审计日志的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<AuditLogDetailVo> getDetail(
      @Parameter(description = "日志ID") @PathVariable String id) {
    return ApiLocaleResult.success(auditLogFacade.getDetail(id));
  }

  @Operation(operationId = "listAuditLogs", summary = "获取审计日志列表", description = "分页获取审计日志列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<AuditLogVo>> list(@Valid AuditLogFindDto dto) {
    return ApiLocaleResult.success(auditLogFacade.list(dto));
  }

  @Operation(operationId = "getAuditLogExportStatus", summary = "获取导出任务状态", description = "获取审计日志导出任务状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "任务不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/export/{taskId}")
  public ApiLocaleResult<AuditLogExportStatusVo> getExportStatus(
      @Parameter(description = "导出任务ID") @PathVariable String taskId) {
    return ApiLocaleResult.success(auditLogFacade.getExportStatus(taskId));
  }

  @Operation(operationId = "getAuditLogUserHistory", summary = "获取用户操作历史", description = "获取指定用户的操作历史记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/user/{userId}")
  public ApiLocaleResult<PageResult<AuditLogVo>> getUserHistory(
      @Parameter(description = "用户ID") @PathVariable String userId,
      @Valid AuditLogUserHistoryDto dto) {
    return ApiLocaleResult.success(auditLogFacade.getUserHistory(userId, dto));
  }

  @Operation(operationId = "getAuditLogSensitiveLogs", summary = "获取敏感操作日志", description = "获取敏感操作日志列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/sensitive")
  public ApiLocaleResult<PageResult<SensitiveLogVo>> getSensitiveLogs(@Valid AuditLogSensitiveFindDto dto) {
    return ApiLocaleResult.success(auditLogFacade.getSensitiveLogs(dto));
  }

  @Operation(operationId = "getAuditLogFailureLogs", summary = "获取失败操作日志", description = "获取操作失败的日志列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/failures")
  public ApiLocaleResult<PageResult<AuditLogVo>> getFailureLogs(@Valid AuditLogFailureFindDto dto) {
    return ApiLocaleResult.success(auditLogFacade.getFailureLogs(dto));
  }

  @Operation(operationId = "getAuditLogRetentionPolicy", summary = "获取审计日志保留策略", description = "获取审计日志保留策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/retention-policy")
  public ApiLocaleResult<RetentionPolicyVo> getRetentionPolicy() {
    return ApiLocaleResult.success(auditLogFacade.getRetentionPolicy());
  }

  @Operation(operationId = "getAuditLogStats", summary = "获取审计日志统计", description = "获取审计日志统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<AuditLogStatsVo> getStats(
      @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
      @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
    return ApiLocaleResult.success(auditLogFacade.getStats(startDate, endDate));
  }

  @Operation(operationId = "getAuditLogModuleStats", summary = "获取模块操作统计", description = "获取各模块的操作统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/module-stats")
  public ApiLocaleResult<List<ModuleStatsVo>> getModuleStats(
      @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
      @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
    return ApiLocaleResult.success(auditLogFacade.getModuleStats(startDate, endDate));
  }
}
