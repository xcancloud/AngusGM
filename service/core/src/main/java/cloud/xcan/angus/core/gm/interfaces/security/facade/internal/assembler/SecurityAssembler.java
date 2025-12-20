package cloud.xcan.angus.core.gm.interfaces.security.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityDetailVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityListVo;
import org.springframework.stereotype.Component;

@Component
public class SecurityAssembler {
    
    public Security toEntity(SecurityCreateDto dto) {
        Security entity = new Security();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setEnabled(dto.getEnabled());
        return entity;
    }
    
    public void updateEntity(Security entity, SecurityUpdateDto dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
    
    public SecurityDetailVo toDetailVo(Security entity) {
        SecurityDetailVo vo = new SecurityDetailVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setEnabled(entity.getEnabled());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
    
    public SecurityListVo toListVo(Security entity) {
        SecurityListVo vo = new SecurityListVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
