package cloud.xcan.angus.core.gm.application.query.auditlog;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuditLogQuery {
    Optional<AuditLog> findById(Long id);
    Page<AuditLog> findAll(Pageable pageable);
    long count();
}
