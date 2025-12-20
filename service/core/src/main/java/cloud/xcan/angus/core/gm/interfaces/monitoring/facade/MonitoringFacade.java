package cloud.xcan.angus.core.gm.interfaces.monitoring.facade;

import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringFindDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringListVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MonitoringFacade {
    MonitoringDetailVo create(MonitoringCreateDto dto);
    MonitoringDetailVo update(Long id, MonitoringUpdateDto dto);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    MonitoringDetailVo findById(Long id);
    Page<MonitoringListVo> find(MonitoringFindDto dto, Pageable pageable);
    MonitoringStatsVo getStats();
}
