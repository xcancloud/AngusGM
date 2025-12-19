package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.apimonitoring.ApiMonitoringCmd;
import cloud.xcan.angus.core.gm.application.query.apimonitoring.ApiMonitoringQuery;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.ApiMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.internal.assembler.ApiMonitoringAssembler;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringListVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiMonitoringFacadeImpl implements ApiMonitoringFacade {
    
    private final ApiMonitoringCmd apiMonitoringCmd;
    private final ApiMonitoringQuery apiMonitoringQuery;
    private final ApiMonitoringAssembler assembler;
    
    @Override
    public ApiMonitoringDetailVo create(ApiMonitoringCreateDto dto) {
        ApiMonitoring entity = assembler.toEntity(dto);
        ApiMonitoring saved = apiMonitoringCmd.create(entity);
        return assembler.toDetailVo(saved);
    }
    
    @Override
    public ApiMonitoringDetailVo update(Long id, ApiMonitoringUpdateDto dto) {
        ApiMonitoring entity = apiMonitoringQuery.findById(id)
            .orElseThrow(() -> new RuntimeException("ApiMonitoring not found"));
        assembler.updateEntity(entity, dto);
        ApiMonitoring updated = apiMonitoringCmd.update(id, entity);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        apiMonitoringCmd.delete(id);
    }
    
    @Override
    public void enable(Long id) {
        apiMonitoringCmd.enable(id);
    }
    
    @Override
    public void disable(Long id) {
        apiMonitoringCmd.disable(id);
    }
    
    @Override
    public ApiMonitoringDetailVo findById(Long id) {
        return apiMonitoringQuery.findById(id)
            .map(assembler::toDetailVo)
            .orElseThrow(() -> new RuntimeException("ApiMonitoring not found"));
    }
    
    @Override
    public Page<ApiMonitoringListVo> find(ApiMonitoringFindDto dto, Pageable pageable) {
        return apiMonitoringQuery.findAll(pageable)
            .map(assembler::toListVo);
    }
    
    @Override
    public ApiMonitoringStatsVo getStats() {
        ApiMonitoringStatsVo stats = new ApiMonitoringStatsVo();
        stats.setTotal(apiMonitoringQuery.count());
        // Add more statistics as needed
        return stats;
    }
}
