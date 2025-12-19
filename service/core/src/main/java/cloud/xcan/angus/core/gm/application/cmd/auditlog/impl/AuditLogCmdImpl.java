package cloud.xcan.angus.core.gm.application.cmd.auditlog.impl;

import cloud.xcan.angus.core.gm.application.cmd.auditlog.AuditLogCmd;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogCmdImpl implements AuditLogCmd {
    
    private final AuditLogRepo auditLogRepo;
    
    @Override
    @Transactional
    public AuditLog create(AuditLog auditLog) {
        return auditLogRepo.save(auditLog);
    }
    
    @Override
    @Transactional
    public AuditLog update(Long id, AuditLog auditLog) {
        AuditLog existing = auditLogRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("AuditLog not found"));
        // Update fields
        return auditLogRepo.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        auditLogRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        AuditLog auditLog = auditLogRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("AuditLog not found"));
        auditLog.setEnabled(true);
        auditLogRepo.save(auditLog);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        AuditLog auditLog = auditLogRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("AuditLog not found"));
        auditLog.setEnabled(false);
        auditLogRepo.save(auditLog);
    }
}
