package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade;

import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringListVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApiMonitoringFacade {
    ApiMonitoringDetailVo create(ApiMonitoringCreateDto dto);
    ApiMonitoringDetailVo update(Long id, ApiMonitoringUpdateDto dto);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    ApiMonitoringDetailVo findById(Long id);
    Page<ApiMonitoringListVo> find(ApiMonitoringFindDto dto, Pageable pageable);
    ApiMonitoringStatsVo getStats();
}
