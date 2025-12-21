package cloud.xcan.angus.core.gm.interfaces.security.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.security.SecurityCmd;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.internal.assembler.SecurityAssembler;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityDetailVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityListVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityStatsVo;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SecurityFacadeImpl implements SecurityFacade {
    
    @Resource
    private SecurityCmd securityCmd;
    
    @Resource
    private SecurityQuery securityQuery;
    
    @Resource
    private SecurityAssembler assembler;
    
    @Override
    public SecurityDetailVo create(SecurityCreateDto dto) {
        Security entity = assembler.toEntity(dto);
        Security saved = securityCmd.create(entity);
        return assembler.toDetailVo(saved);
    }
    
    @Override
    public SecurityDetailVo update(Long id, SecurityUpdateDto dto) {
        Security entity = securityQuery.findById(id)
            .orElseThrow(() -> new RuntimeException("Security not found"));
        assembler.updateEntity(entity, dto);
        Security updated = securityCmd.update(id, entity);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        securityCmd.delete(id);
    }
    
    @Override
    public void enable(Long id) {
        securityCmd.enable(id);
    }
    
    @Override
    public void disable(Long id) {
        securityCmd.disable(id);
    }
    
    @Override
    public SecurityDetailVo findById(Long id) {
        return securityQuery.findById(id)
            .map(assembler::toDetailVo)
            .orElseThrow(() -> new RuntimeException("Security not found"));
    }
    
    @Override
    public Page<SecurityListVo> find(SecurityFindDto dto, Pageable pageable) {
        return securityQuery.findAll(pageable)
            .map(assembler::toListVo);
    }
    
    @Override
    public SecurityStatsVo getStats() {
        SecurityStatsVo stats = new SecurityStatsVo();
        stats.setTotal(securityQuery.count());
        // Add more statistics as needed
        return stats;
    }
}
