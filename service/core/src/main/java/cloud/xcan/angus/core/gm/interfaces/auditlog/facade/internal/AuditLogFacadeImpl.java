package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.auditlog.AuditLogCmd;
import cloud.xcan.angus.core.gm.application.query.auditlog.AuditLogQuery;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.AuditLogFacade;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.internal.assembler.AuditLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogListVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AuditLogFacadeImpl implements AuditLogFacade {
    
    @Resource
    private AuditLogCmd auditLogCmd;
    
    @Resource
    private AuditLogQuery auditLogQuery;
    
    @Resource
    private AuditLogAssembler assembler;
    
    @Override
    public AuditLogDetailVo create(AuditLogCreateDto dto) {
        AuditLog entity = assembler.toEntity(dto);
        AuditLog saved = auditLogCmd.create(entity);
        return assembler.toDetailVo(saved);
    }
    
    @Override
    public AuditLogDetailVo update(Long id, AuditLogUpdateDto dto) {
        AuditLog entity = auditLogQuery.findById(id)
            .orElseThrow(() -> new RuntimeException("AuditLog not found"));
        assembler.updateEntity(entity, dto);
        AuditLog updated = auditLogCmd.update(id, entity);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        auditLogCmd.delete(id);
    }
    
    @Override
    public void enable(Long id) {
        auditLogCmd.enable(id);
    }
    
    @Override
    public void disable(Long id) {
        auditLogCmd.disable(id);
    }
    
    @Override
    public AuditLogDetailVo findById(Long id) {
        return auditLogQuery.findById(id)
            .map(assembler::toDetailVo)
            .orElseThrow(() -> new RuntimeException("AuditLog not found"));
    }
    
    @Override
    public Page<AuditLogListVo> find(AuditLogFindDto dto, Pageable pageable) {
        return auditLogQuery.findAll(pageable)
            .map(assembler::toListVo);
    }
    
    @Override
    public AuditLogStatsVo getStats() {
        AuditLogStatsVo stats = new AuditLogStatsVo();
        stats.setTotal(auditLogQuery.count());
        // Add more statistics as needed
        return stats;
    }
}
