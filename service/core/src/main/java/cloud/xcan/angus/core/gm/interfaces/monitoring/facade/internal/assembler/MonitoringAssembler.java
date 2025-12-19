package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringListVo;
import org.springframework.stereotype.Component;

@Component
public class MonitoringAssembler {
    
    public Monitoring toEntity(MonitoringCreateDto dto) {
        Monitoring entity = new Monitoring();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setEnabled(dto.getEnabled());
        return entity;
    }
    
    public void updateEntity(Monitoring entity, MonitoringUpdateDto dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
    
    public MonitoringDetailVo toDetailVo(Monitoring entity) {
        MonitoringDetailVo vo = new MonitoringDetailVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setEnabled(entity.getEnabled());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
    
    public MonitoringListVo toListVo(Monitoring entity) {
        MonitoringListVo vo = new MonitoringListVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
