package cloud.xcan.angus.core.gm.application.query.auditlog;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.ModuleStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface AuditLogQuery {
    
    AuditLog findAndCheck(Long id);
    
    Page<AuditLog> find(GenericSpecification<AuditLog> spec, PageRequest pageable);
    
    Page<AuditLog> findByUserId(Long userId, GenericSpecification<AuditLog> spec, PageRequest pageable);
    
    Page<AuditLog> findSensitiveLogs(GenericSpecification<AuditLog> spec, PageRequest pageable);
    
    Page<AuditLog> findFailureLogs(GenericSpecification<AuditLog> spec, PageRequest pageable);
    
    /**
     * Get statistics for audit logs
     */
    AuditLogStatsVo getStats(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get module statistics
     */
    List<ModuleStatsVo> getModuleStats(LocalDateTime startDate, LocalDateTime endDate);
}
