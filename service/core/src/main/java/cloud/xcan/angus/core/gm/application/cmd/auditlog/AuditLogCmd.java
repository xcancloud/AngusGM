package cloud.xcan.angus.core.gm.application.cmd.auditlog;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;

public interface AuditLogCmd {
    AuditLog create(AuditLog auditLog);
    AuditLog update(Long id, AuditLog auditLog);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
}
