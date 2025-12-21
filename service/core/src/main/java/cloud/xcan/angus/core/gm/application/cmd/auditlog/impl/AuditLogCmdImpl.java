package cloud.xcan.angus.core.gm.application.cmd.auditlog.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.auditlog.AuditLogCmd;
import cloud.xcan.angus.core.gm.application.query.auditlog.AuditLogQuery;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLogRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * Audit log command service implementation
 * </p>
 */
@Service
public class AuditLogCmdImpl implements AuditLogCmd {
    
    @Resource
    private AuditLogRepo auditLogRepo;
    
    @Resource
    private AuditLogQuery auditLogQuery;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditLog create(AuditLog auditLog) {
        return new BizTemplate<AuditLog>() {
            @Override
            protected void checkParams() {
                // Validate required fields
                if (auditLog == null) {
                    throw ResourceNotFound.of("审计日志不能为空", new Object[]{});
                }
                if (auditLog.getModule() == null || auditLog.getModule().trim().isEmpty()) {
                    throw ResourceNotFound.of("模块不能为空", new Object[]{});
                }
                if (auditLog.getOperation() == null || auditLog.getOperation().trim().isEmpty()) {
                    throw ResourceNotFound.of("操作类型不能为空", new Object[]{});
                }
            }

            @Override
            protected AuditLog process() {
                // Set default values
                if (auditLog.getSuccess() == null) {
                    auditLog.setSuccess(true);
                }
                if (auditLog.getOperationTime() == null) {
                    auditLog.setOperationTime(LocalDateTime.now());
                }
                return auditLogRepo.save(auditLog);
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditLog update(Long id, AuditLog auditLog) {
        return new BizTemplate<AuditLog>() {
            AuditLog existing;
            
            @Override
            protected void checkParams() {
                // Check if audit log exists
                existing = auditLogQuery.findAndCheck(id);
                
                if (auditLog == null) {
                    throw ResourceNotFound.of("审计日志不能为空", new Object[]{});
                }
            }

            @Override
            protected AuditLog process() {
                // Update fields if provided
                if (auditLog.getModule() != null) {
                    existing.setModule(auditLog.getModule());
                }
                if (auditLog.getOperation() != null) {
                    existing.setOperation(auditLog.getOperation());
                }
                if (auditLog.getLevel() != null) {
                    existing.setLevel(auditLog.getLevel());
                }
                if (auditLog.getDescription() != null) {
                    existing.setDescription(auditLog.getDescription());
                }
                if (auditLog.getSuccess() != null) {
                    existing.setSuccess(auditLog.getSuccess());
                }
                if (auditLog.getOperationTime() != null) {
                    existing.setOperationTime(auditLog.getOperationTime());
                }
                if (auditLog.getDuration() != null) {
                    existing.setDuration(auditLog.getDuration());
                }
                
                return auditLogRepo.save(existing);
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                // Check if audit log exists
                auditLogQuery.findAndCheck(id);
            }

            @Override
            protected Void process() {
                auditLogRepo.deleteById(id);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanup(String level, LocalDateTime beforeDate) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                // Validate date parameter
                if (beforeDate == null) {
                    throw ResourceNotFound.of("清理日期不能为空", new Object[]{});
                }
                // Validate date is not in the future
                if (beforeDate.isAfter(LocalDateTime.now())) {
                    throw ResourceNotFound.of("清理日期不能晚于当前时间", new Object[]{});
                }
            }

            @Override
            protected Void process() {
                if (level != null && !level.trim().isEmpty()) {
                    auditLogRepo.deleteByLevelAndCreatedDateBefore(level, beforeDate);
                } else {
                    auditLogRepo.deleteByCreatedDateBefore(beforeDate);
                }
                return null;
            }
        }.execute();
    }
}
