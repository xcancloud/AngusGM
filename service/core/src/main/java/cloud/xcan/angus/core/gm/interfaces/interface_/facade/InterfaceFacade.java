package cloud.xcan.angus.core.gm.interfaces.interface_.facade;

import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceStatsVo;
import org.springframework.data.domain.Page;

public interface InterfaceFacade {
    InterfaceDetailVo create(InterfaceCreateDto dto);
    InterfaceDetailVo update(InterfaceUpdateDto dto);
    void enable(String id);
    void disable(String id);
    void delete(String id);
    InterfaceDetailVo get(String id);
    Page<InterfaceListVo> find(InterfaceFindDto dto);
    InterfaceStatsVo stats();
}
