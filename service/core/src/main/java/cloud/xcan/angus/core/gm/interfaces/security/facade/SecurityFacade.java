package cloud.xcan.angus.core.gm.interfaces.security.facade;

import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityDetailVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityListVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecurityFacade {
    SecurityDetailVo create(SecurityCreateDto dto);
    SecurityDetailVo update(Long id, SecurityUpdateDto dto);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    SecurityDetailVo findById(Long id);
    Page<SecurityListVo> find(SecurityFindDto dto, Pageable pageable);
    SecurityStatsVo getStats();
}
