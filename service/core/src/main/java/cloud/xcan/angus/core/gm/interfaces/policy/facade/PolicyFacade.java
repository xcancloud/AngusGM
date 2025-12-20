package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyStatsVo;
import org.springframework.data.domain.Page;

public interface PolicyFacade {
    PolicyDetailVo create(PolicyCreateDto dto);
    PolicyDetailVo update(PolicyUpdateDto dto);
    void enable(Long id);
    void disable(Long id);
    void delete(Long id);
    PolicyDetailVo getById(Long id);
    Page<PolicyListVo> find(PolicyFindDto dto);
    PolicyStatsVo getStats();
}
