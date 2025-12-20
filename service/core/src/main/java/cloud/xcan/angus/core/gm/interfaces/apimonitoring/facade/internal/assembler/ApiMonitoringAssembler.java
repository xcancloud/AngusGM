package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringListVo;
import org.springframework.stereotype.Component;

@Component
public class ApiMonitoringAssembler {
    
    public ApiMonitoring toEntity(ApiMonitoringCreateDto dto) {
        ApiMonitoring entity = new ApiMonitoring();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setEnabled(dto.getEnabled());
        return entity;
    }
    
    public void updateEntity(ApiMonitoring entity, ApiMonitoringUpdateDto dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
    
    public ApiMonitoringDetailVo toDetailVo(ApiMonitoring entity) {
        ApiMonitoringDetailVo vo = new ApiMonitoringDetailVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setEnabled(entity.getEnabled());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
    
    public ApiMonitoringListVo toListVo(ApiMonitoring entity) {
        ApiMonitoringListVo vo = new ApiMonitoringListVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
