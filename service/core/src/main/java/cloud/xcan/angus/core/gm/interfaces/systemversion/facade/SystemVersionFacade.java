package cloud.xcan.angus.core.gm.interfaces.systemversion.facade;

import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionCreateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionListVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SystemVersionFacade {
    SystemVersionDetailVo create(SystemVersionCreateDto dto);
    SystemVersionDetailVo update(Long id, SystemVersionUpdateDto dto);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    SystemVersionDetailVo findById(Long id);
    Page<SystemVersionListVo> find(SystemVersionFindDto dto, Pageable pageable);
    SystemVersionStatsVo getStats();
}
