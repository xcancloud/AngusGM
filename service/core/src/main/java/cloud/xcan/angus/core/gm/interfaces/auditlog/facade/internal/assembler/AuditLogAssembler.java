package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogListVo;
import org.springframework.stereotype.Component;

@Component
public class AuditLogAssembler {
    
    public AuditLog toEntity(AuditLogCreateDto dto) {
        AuditLog entity = new AuditLog();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setEnabled(dto.getEnabled());
        return entity;
    }
    
    public void updateEntity(AuditLog entity, AuditLogUpdateDto dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
    
    public AuditLogDetailVo toDetailVo(AuditLog entity) {
        AuditLogDetailVo vo = new AuditLogDetailVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setEnabled(entity.getEnabled());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
    
    public AuditLogListVo toListVo(AuditLog entity) {
        AuditLogListVo vo = new AuditLogListVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
