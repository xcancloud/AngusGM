package cloud.xcan.angus.core.gm.application.query.auditlog.impl;

import cloud.xcan.angus.core.gm.application.query.auditlog.AuditLogQuery;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogQueryImpl implements AuditLogQuery {
    
    private final AuditLogRepo auditLogRepo;
    
    @Override
    public Optional<AuditLog> findById(Long id) {
        return auditLogRepo.findById(id);
    }
    
    @Override
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepo.findAll(pageable);
    }
    
    @Override
    public long count() {
        return auditLogRepo.count();
    }
}
