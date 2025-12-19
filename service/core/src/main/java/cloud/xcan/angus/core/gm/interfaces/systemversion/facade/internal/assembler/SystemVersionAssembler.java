package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionCreateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionListVo;
import org.springframework.stereotype.Component;

@Component
public class SystemVersionAssembler {
    
    public SystemVersion toEntity(SystemVersionCreateDto dto) {
        SystemVersion entity = new SystemVersion();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setEnabled(dto.getEnabled());
        return entity;
    }
    
    public void updateEntity(SystemVersion entity, SystemVersionUpdateDto dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
    
    public SystemVersionDetailVo toDetailVo(SystemVersion entity) {
        SystemVersionDetailVo vo = new SystemVersionDetailVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setEnabled(entity.getEnabled());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
    
    public SystemVersionListVo toListVo(SystemVersion entity) {
        SystemVersionListVo vo = new SystemVersionListVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
