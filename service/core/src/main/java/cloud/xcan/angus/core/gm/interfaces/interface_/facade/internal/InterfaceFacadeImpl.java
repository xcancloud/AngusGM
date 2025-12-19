package cloud.xcan.angus.core.gm.interfaces.interface_.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.interface_.InterfaceCmd;
import cloud.xcan.angus.core.gm.application.query.interface_.InterfaceQuery;
import cloud.xcan.angus.core.gm.domain.interface_.Interface;
import cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.InterfaceFacade;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.internal.assembler.InterfaceAssembler;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InterfaceFacadeImpl implements InterfaceFacade {
    private final InterfaceCmd interfaceCmd;
    private final InterfaceQuery interfaceQuery;
    private final InterfaceAssembler interfaceAssembler;

    @Override
    public InterfaceDetailVo create(InterfaceCreateDto dto) {
        Interface interface_ = interfaceAssembler.toEntity(dto);
        Interface created = interfaceCmd.create(interface_);
        return interfaceAssembler.toDetailVo(created);
    }

    @Override
    public InterfaceDetailVo update(InterfaceUpdateDto dto) {
        Interface interface_ = interfaceAssembler.toEntity(dto);
        Interface updated = interfaceCmd.update(interface_);
        return interfaceAssembler.toDetailVo(updated);
    }

    @Override
    public void enable(String id) {
        interfaceCmd.enable(id);
    }

    @Override
    public void disable(String id) {
        interfaceCmd.disable(id);
    }

    @Override
    public void delete(String id) {
        interfaceCmd.delete(id);
    }

    @Override
    public InterfaceDetailVo get(String id) {
        Interface interface_ = interfaceQuery.get(id);
        return interfaceAssembler.toDetailVo(interface_);
    }

    @Override
    public Page<InterfaceListVo> find(InterfaceFindDto dto) {
        Page<Interface> interfaces = interfaceQuery.find(dto);
        return interfaces.map(interfaceAssembler::toListVo);
    }

    @Override
    public InterfaceStatsVo stats() {
        InterfaceStatsVo stats = new InterfaceStatsVo();
        stats.setTotal(interfaceQuery.countTotal());
        stats.setEnabled(interfaceQuery.countByStatus(InterfaceStatus.ENABLED));
        stats.setDisabled(interfaceQuery.countByStatus(InterfaceStatus.DISABLED));
        
        Map<String, Long> methodDist = new HashMap<>();
        methodDist.put("GET", 0L);
        methodDist.put("POST", 0L);
        methodDist.put("PUT", 0L);
        methodDist.put("PATCH", 0L);
        methodDist.put("DELETE", 0L);
        stats.setMethodDistribution(methodDist);
        stats.setAuthRequiredCount(0L);
        
        return stats;
    }
}
