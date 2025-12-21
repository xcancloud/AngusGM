package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.query.auditlog.AuditLogQuery;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.AuditLogFacade;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogCleanupDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogExportDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFailureFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogSensitiveFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUserHistoryDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.RetentionPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.internal.assembler.AuditLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogExportResultVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogExportStatusVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.ModuleStatsVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.RetentionPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.SensitiveLogVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Audit log facade implementation
 * </p>
 */
@Component
public class AuditLogFacadeImpl implements AuditLogFacade {

  @Resource
  private AuditLogQuery auditLogQuery;

  // In-memory storage for export tasks (in production, should use database or cache)
  private final Map<String, AuditLogExportStatusVo> exportTasks = new ConcurrentHashMap<>();

  // In-memory storage for retention policy (in production, should use database or config)
  private RetentionPolicyVo retentionPolicy;

  @Override
  public AuditLogStatsVo getStats(String startDate, String endDate) {
    LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return auditLogQuery.getStats(start, end);
  }

  @Override
  public PageResult<AuditLogVo> list(AuditLogFindDto dto) {
    GenericSpecification<AuditLog> spec = AuditLogAssembler.getSpecification(dto);
    Page<AuditLog> page = auditLogQuery.find(spec, dto.tranPage());
    return buildVoPageResult(page, AuditLogAssembler::toVo);
  }

  @Override
  public AuditLogDetailVo getDetail(String id) {
    AuditLog log = auditLogQuery.findAndCheck(Long.parseLong(id));
    return AuditLogAssembler.toDetailVo(log);
  }

  @Override
  public AuditLogExportResultVo export(AuditLogExportDto dto) {
    String taskId = "EXPORT_" + System.currentTimeMillis();
    LocalDateTime createTime = LocalDateTime.now();
    
    // Create export task status
    AuditLogExportStatusVo status = new AuditLogExportStatusVo();
    status.setTaskId(taskId);
    status.setStatus("进行中");
    status.setProgress(0);
    status.setTotalRecords(0L);
    status.setCreateTime(createTime);
    status.setExpiryTime(createTime.plusDays(7));
    
    // Store task (in production, should persist to database)
    exportTasks.put(taskId, status);
    
    // Create result
    AuditLogExportResultVo result = new AuditLogExportResultVo();
    result.setTaskId(taskId);
    result.setStatus("进行中");
    result.setCreateTime(createTime);
    
    // Estimate time based on date range
    if (dto.getStartDate() != null && dto.getEndDate() != null) {
      try {
        LocalDateTime start = LocalDateTime.parse(dto.getStartDate() + " 00:00:00", 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(dto.getEndDate() + " 23:59:59", 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long days = java.time.Duration.between(start, end).toDays();
        if (days > 30) {
          result.setEstimatedTime("10分钟");
        } else if (days > 7) {
          result.setEstimatedTime("5分钟");
        } else {
          result.setEstimatedTime("2分钟");
        }
      } catch (Exception e) {
        result.setEstimatedTime("2分钟");
      }
    } else {
      result.setEstimatedTime("2分钟");
    }
    
    // TODO: In production, trigger async export job here
    // For now, simulate completion after a delay
    simulateExportCompletion(taskId, dto);
    
    return result;
  }

  /**
   * Simulate export completion (in production, this should be handled by async job)
   */
  private void simulateExportCompletion(String taskId, AuditLogExportDto dto) {
    // In production, this should be handled by an async job that:
    // 1. Queries audit logs based on dto filters
    // 2. Exports to Excel/CSV file
    // 3. Uploads to file storage
    // 4. Updates task status
        
    // For now, simulate immediate completion
    AuditLogExportStatusVo status = exportTasks.get(taskId);
    if (status != null) {
      status.setStatus("完成");
      status.setProgress(100);
      status.setTotalRecords(1000L); // Simulated count
      status.setCompleteTime(LocalDateTime.now());
      status.setFileSize("2.5 MB");
      status.setDownloadUrl("/api/v1/audit-logs/export/" + taskId + "/download");
    }
  }

  @Override
  public AuditLogExportStatusVo getExportStatus(String taskId) {
    AuditLogExportStatusVo status = exportTasks.get(taskId);
    if (status == null) {
      // Task not found, return default status
      status = new AuditLogExportStatusVo();
      status.setTaskId(taskId);
      status.setStatus("未找到");
      status.setProgress(0);
      status.setTotalRecords(0L);
    }
    return status;
  }

  @Override
  public PageResult<AuditLogVo> getUserHistory(String userId, AuditLogUserHistoryDto dto) {
    GenericSpecification<AuditLog> spec = AuditLogAssembler.getUserHistorySpecification(Long.parseLong(userId), dto);
    Page<AuditLog> page = auditLogQuery.findByUserId(Long.parseLong(userId), spec, dto.tranPage());
    return buildVoPageResult(page, AuditLogAssembler::toVo);
  }

  @Override
  public List<ModuleStatsVo> getModuleStats(String startDate, String endDate) {
    LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return auditLogQuery.getModuleStats(start, end);
  }

  @Override
  public PageResult<SensitiveLogVo> getSensitiveLogs(AuditLogSensitiveFindDto dto) {
    GenericSpecification<AuditLog> spec = AuditLogAssembler.getSensitiveLogsSpecification(dto);
    PageRequest pageRequest = PageRequest.of(
        dto.getPage() != null ? dto.getPage() - 1 : 0,
        dto.getSize() != null ? dto.getSize() : 20
    );
    Page<AuditLog> page = auditLogQuery.findSensitiveLogs(spec, pageRequest);
    return buildVoPageResult(page, AuditLogAssembler::toSensitiveLogVo);
  }

  @Override
  public PageResult<AuditLogVo> getFailureLogs(AuditLogFailureFindDto dto) {
    GenericSpecification<AuditLog> spec = AuditLogAssembler.getFailureLogsSpecification(dto);
    Page<AuditLog> page = auditLogQuery.findFailureLogs(spec, dto.tranPage());
    return buildVoPageResult(page, AuditLogAssembler::toVo);
  }

  @Override
  public void cleanup(AuditLogCleanupDto dto) {
    LocalDateTime beforeDate = LocalDateTime.parse(dto.getBeforeDate() + " 00:00:00", 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    auditLogQuery.cleanup(dto.getLevel(), beforeDate);
  }

  @Override
  public RetentionPolicyVo getRetentionPolicy() {
    // Initialize default policy if not exists
    if (retentionPolicy == null) {
      retentionPolicy = new RetentionPolicyVo();
      retentionPolicy.setId("RETENTION_001");
      retentionPolicy.setInfoRetentionDays(90);
      retentionPolicy.setWarningRetentionDays(180);
      retentionPolicy.setErrorRetentionDays(365);
      retentionPolicy.setAutoCleanupEnabled(true);
      retentionPolicy.setCleanupSchedule("0 2 * * *");
    }
    return retentionPolicy;
  }

  @Override
  public RetentionPolicyVo updateRetentionPolicy(RetentionPolicyUpdateDto dto) {
    // Get or create policy
    RetentionPolicyVo policy = getRetentionPolicy();
    
    // Update policy
    if (dto.getInfoRetentionDays() != null) {
      policy.setInfoRetentionDays(dto.getInfoRetentionDays());
    }
    if (dto.getWarningRetentionDays() != null) {
      policy.setWarningRetentionDays(dto.getWarningRetentionDays());
    }
    if (dto.getErrorRetentionDays() != null) {
      policy.setErrorRetentionDays(dto.getErrorRetentionDays());
    }
    if (dto.getAutoCleanupEnabled() != null) {
      policy.setAutoCleanupEnabled(dto.getAutoCleanupEnabled());
    }
    if (dto.getCleanupSchedule() != null) {
      policy.setCleanupSchedule(dto.getCleanupSchedule());
    }
    
    // Store updated policy (in production, should persist to database)
    retentionPolicy = policy;
    
    // TODO: In production, update scheduled cleanup job based on new schedule
    
    return policy;
  }
}
