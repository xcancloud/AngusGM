package cloud.xcan.angus.core.gm.application.cmd.auditlog;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import java.time.LocalDateTime;

public interface AuditLogCmd {
    AuditLog create(AuditLog auditLog);
    AuditLog update(Long id, AuditLog auditLog);
    void delete(Long id);
    
    /**
     * Cleanup audit logs by level and date
     */
    void cleanup(String level, LocalDateTime beforeDate);
}
