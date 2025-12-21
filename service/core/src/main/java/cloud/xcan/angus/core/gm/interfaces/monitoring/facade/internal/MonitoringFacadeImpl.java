package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.monitoring.MonitoringCmd;
import cloud.xcan.angus.core.gm.application.query.monitoring.MonitoringQuery;
import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.MonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringFindDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.internal.assembler.MonitoringAssembler;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringListVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringStatsVo;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MonitoringFacadeImpl implements MonitoringFacade {
    
    @Resource
    private MonitoringCmd monitoringCmd;
    
    @Resource
    private MonitoringQuery monitoringQuery;
    
    @Resource
    private MonitoringAssembler assembler;
    
    @Override
    public MonitoringDetailVo create(MonitoringCreateDto dto) {
        Monitoring entity = assembler.toEntity(dto);
        Monitoring saved = monitoringCmd.create(entity);
        return assembler.toDetailVo(saved);
    }
    
    @Override
    public MonitoringDetailVo update(Long id, MonitoringUpdateDto dto) {
        Monitoring entity = monitoringQuery.findById(id)
            .orElseThrow(() -> new RuntimeException("Monitoring not found"));
        assembler.updateEntity(entity, dto);
        Monitoring updated = monitoringCmd.update(id, entity);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        monitoringCmd.delete(id);
    }
    
    @Override
    public void enable(Long id) {
        monitoringCmd.enable(id);
    }
    
    @Override
    public void disable(Long id) {
        monitoringCmd.disable(id);
    }
    
    @Override
    public MonitoringDetailVo findById(Long id) {
        return monitoringQuery.findById(id)
            .map(assembler::toDetailVo)
            .orElseThrow(() -> new RuntimeException("Monitoring not found"));
    }
    
    @Override
    public Page<MonitoringListVo> find(MonitoringFindDto dto, Pageable pageable) {
        return monitoringQuery.findAll(pageable)
            .map(assembler::toListVo);
    }
    
    @Override
    public MonitoringStatsVo getStats() {
        MonitoringStatsVo stats = new MonitoringStatsVo();
        stats.setTotal(monitoringQuery.count());
        // Add more statistics as needed
        return stats;
    }
}
