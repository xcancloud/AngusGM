package cloud.xcan.angus.core.gm.interfaces.auditlog;

import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.AuditLogFacade;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.*;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审计日志REST接口
 * 
 * 系统操作日志记录、查询、导出
 */
@Tag(name = "AuditLogs", description = "审计日志 - 系统操作日志记录、查询、导出")
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogRest {
    
    private final AuditLogFacade auditLogFacade;
    
    // ==================== 统计与查询 ====================
    
    @Operation(summary = "获取审计日志统计", description = "获取审计日志统计信息")
    @GetMapping("/stats")
    public ApiLocaleResult<AuditLogStatsVo> getStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        return ApiLocaleResult.success(auditLogFacade.getStats(startDate, endDate));
    }
    
    @Operation(summary = "获取审计日志列表", description = "分页获取审计日志列表")
    @GetMapping
    public ApiLocaleResult<PageResult<AuditLogVo>> list(@Valid AuditLogFindDto dto) {
        return ApiLocaleResult.success(auditLogFacade.list(dto));
    }
    
    @Operation(summary = "获取审计日志详情", description = "获取指定审计日志的详细信息")
    @GetMapping("/{id}")
    public ApiLocaleResult<AuditLogDetailVo> getDetail(
            @Parameter(description = "日志ID") @PathVariable String id) {
        return ApiLocaleResult.success(auditLogFacade.getDetail(id));
    }
    
    // ==================== 导出 ====================
    
    @Operation(summary = "导出审计日志", description = "创建审计日志导出任务")
    @PostMapping("/export")
    public ApiLocaleResult<AuditLogExportResultVo> export(@Valid @RequestBody AuditLogExportDto dto) {
        return ApiLocaleResult.success(auditLogFacade.export(dto));
    }
    
    @Operation(summary = "获取导出任务状态", description = "获取审计日志导出任务状态")
    @GetMapping("/export/{taskId}")
    public ApiLocaleResult<AuditLogExportStatusVo> getExportStatus(
            @Parameter(description = "导出任务ID") @PathVariable String taskId) {
        return ApiLocaleResult.success(auditLogFacade.getExportStatus(taskId));
    }
    
    // ==================== 专项查询 ====================
    
    @Operation(summary = "获取用户操作历史", description = "获取指定用户的操作历史记录")
    @GetMapping("/user/{userId}")
    public ApiLocaleResult<PageResult<AuditLogVo>> getUserHistory(
            @Parameter(description = "用户ID") @PathVariable String userId,
            @Valid AuditLogUserHistoryDto dto) {
        return ApiLocaleResult.success(auditLogFacade.getUserHistory(userId, dto));
    }
    
    @Operation(summary = "获取模块操作统计", description = "获取各模块的操作统计信息")
    @GetMapping("/module-stats")
    public ApiLocaleResult<List<ModuleStatsVo>> getModuleStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        return ApiLocaleResult.success(auditLogFacade.getModuleStats(startDate, endDate));
    }
    
    @Operation(summary = "获取敏感操作日志", description = "获取敏感操作日志列表")
    @GetMapping("/sensitive")
    public ApiLocaleResult<PageResult<SensitiveLogVo>> getSensitiveLogs(@Valid AuditLogSensitiveFindDto dto) {
        return ApiLocaleResult.success(auditLogFacade.getSensitiveLogs(dto));
    }
    
    @Operation(summary = "获取失败操作日志", description = "获取操作失败的日志列表")
    @GetMapping("/failures")
    public ApiLocaleResult<PageResult<AuditLogVo>> getFailureLogs(@Valid AuditLogFailureFindDto dto) {
        return ApiLocaleResult.success(auditLogFacade.getFailureLogs(dto));
    }
    
    // ==================== 日志管理 ====================
    
    @Operation(summary = "清理审计日志", description = "清理指定日期之前的审计日志")
    @DeleteMapping("/cleanup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "清理完成")
    public void cleanup(@Valid @RequestBody AuditLogCleanupDto dto) {
        auditLogFacade.cleanup(dto);
    }
    
    @Operation(summary = "获取审计日志保留策略", description = "获取审计日志保留策略配置")
    @GetMapping("/retention-policy")
    public ApiLocaleResult<RetentionPolicyVo> getRetentionPolicy() {
        return ApiLocaleResult.success(auditLogFacade.getRetentionPolicy());
    }
    
    @Operation(summary = "更新审计日志保留策略", description = "更新审计日志保留策略配置")
    @PutMapping("/retention-policy")
    public ApiLocaleResult<RetentionPolicyVo> updateRetentionPolicy(@Valid @RequestBody RetentionPolicyUpdateDto dto) {
        return ApiLocaleResult.success(auditLogFacade.updateRetentionPolicy(dto));
    }
}
