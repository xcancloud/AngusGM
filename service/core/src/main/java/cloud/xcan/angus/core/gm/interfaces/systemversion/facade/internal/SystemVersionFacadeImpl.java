package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.systemversion.SystemVersionCmd;
import cloud.xcan.angus.core.gm.application.query.systemversion.SystemVersionQuery;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.SystemVersionFacade;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionCreateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.internal.assembler.SystemVersionAssembler;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionListVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemVersionFacadeImpl implements SystemVersionFacade {
    
    private final SystemVersionCmd systemVersionCmd;
    private final SystemVersionQuery systemVersionQuery;
    private final SystemVersionAssembler assembler;
    
    @Override
    public SystemVersionDetailVo create(SystemVersionCreateDto dto) {
        SystemVersion entity = assembler.toEntity(dto);
        SystemVersion saved = systemVersionCmd.create(entity);
        return assembler.toDetailVo(saved);
    }
    
    @Override
    public SystemVersionDetailVo update(Long id, SystemVersionUpdateDto dto) {
        SystemVersion entity = systemVersionQuery.findById(id)
            .orElseThrow(() -> new RuntimeException("SystemVersion not found"));
        assembler.updateEntity(entity, dto);
        SystemVersion updated = systemVersionCmd.update(id, entity);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        systemVersionCmd.delete(id);
    }
    
    @Override
    public void enable(Long id) {
        systemVersionCmd.enable(id);
    }
    
    @Override
    public void disable(Long id) {
        systemVersionCmd.disable(id);
    }
    
    @Override
    public SystemVersionDetailVo findById(Long id) {
        return systemVersionQuery.findById(id)
            .map(assembler::toDetailVo)
            .orElseThrow(() -> new RuntimeException("SystemVersion not found"));
    }
    
    @Override
    public Page<SystemVersionListVo> find(SystemVersionFindDto dto, Pageable pageable) {
        return systemVersionQuery.findAll(pageable)
            .map(assembler::toListVo);
    }
    
    @Override
    public SystemVersionStatsVo getStats() {
        SystemVersionStatsVo stats = new SystemVersionStatsVo();
        stats.setTotal(systemVersionQuery.count());
        // Add more statistics as needed
        return stats;
    }
}
