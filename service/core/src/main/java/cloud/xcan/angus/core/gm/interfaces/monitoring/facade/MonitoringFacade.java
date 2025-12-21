package cloud.xcan.angus.core.gm.interfaces.monitoring.facade;

import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.*;

import java.util.List;

public interface MonitoringFacade {
    
    // ==================== 系统概览 ====================
    
    MonitoringOverviewVo getOverview();
    
    HealthCheckVo getHealth();
    
    // ==================== 资源监控 ====================
    
    CpuUsageVo getCpuUsage(String period);
    
    MemoryUsageVo getMemoryUsage(String period);
    
    DiskUsageVo getDiskUsage();
    
    NetworkUsageVo getNetworkUsage(String period);
    
    PageResult<ProcessInfoVo> getProcesses(ProcessFindDto dto);
    
    // ==================== 数据库监控 ====================
    
    List<DatabasePoolVo> getDatabasePools();
    
    DatabasePerformanceVo getDatabasePerformance(String period);
    
    // ==================== 缓存监控 ====================
    
    RedisMonitorVo getRedisMonitor();
    
    // ==================== 告警规则 ====================
    
    PageResult<AlertRuleVo> listAlertRules(AlertRuleFindDto dto);
    
    AlertRuleVo createAlertRule(AlertRuleCreateDto dto);
    
    // ==================== 告警记录 ====================
    
    PageResult<AlertRecordVo> listAlertRecords(AlertRecordFindDto dto);
    
    AlertHandleVo handleAlertRecord(Long id, AlertHandleDto dto);
}
