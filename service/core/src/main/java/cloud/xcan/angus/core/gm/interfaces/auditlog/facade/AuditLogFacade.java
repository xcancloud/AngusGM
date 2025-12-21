package cloud.xcan.angus.core.gm.interfaces.auditlog.facade;

import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;

/**
 * 审计日志门面接口
 */
public interface AuditLogFacade {
    
    // ==================== 统计与查询 ====================
    
    /**
     * 获取审计日志统计
     */
    AuditLogStatsVo getStats(String startDate, String endDate);
    
    /**
     * 获取审计日志列表
     */
    PageResult<AuditLogVo> list(AuditLogFindDto dto);
    
    /**
     * 获取审计日志详情
     */
    AuditLogDetailVo getDetail(String id);
    
    // ==================== 导出 ====================
    
    /**
     * 导出审计日志
     */
    AuditLogExportResultVo export(AuditLogExportDto dto);
    
    /**
     * 获取导出任务状态
     */
    AuditLogExportStatusVo getExportStatus(String taskId);
    
    // ==================== 专项查询 ====================
    
    /**
     * 获取用户操作历史
     */
    PageResult<AuditLogVo> getUserHistory(String userId, AuditLogUserHistoryDto dto);
    
    /**
     * 获取模块操作统计
     */
    List<ModuleStatsVo> getModuleStats(String startDate, String endDate);
    
    /**
     * 获取敏感操作日志
     */
    PageResult<SensitiveLogVo> getSensitiveLogs(AuditLogSensitiveFindDto dto);
    
    /**
     * 获取失败操作日志
     */
    PageResult<AuditLogVo> getFailureLogs(AuditLogFailureFindDto dto);
    
    // ==================== 日志管理 ====================
    
    /**
     * 清理审计日志
     */
    void cleanup(AuditLogCleanupDto dto);
    
    /**
     * 获取保留策略
     */
    RetentionPolicyVo getRetentionPolicy();
    
    /**
     * 更新保留策略
     */
    RetentionPolicyVo updateRetentionPolicy(RetentionPolicyUpdateDto dto);
}
