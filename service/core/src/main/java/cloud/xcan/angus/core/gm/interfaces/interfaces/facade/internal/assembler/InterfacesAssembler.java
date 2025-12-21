package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo;
import org.springframework.stereotype.Component;

@Component
public class InterfaceAssembler {
    public Interface toEntity(InterfaceCreateDto dto) {
        Interface interface_ = new Interface();
        interface_.setName(dto.getName());
        interface_.setPath(dto.getPath());
        interface_.setMethod(dto.getMethod());
        interface_.setDescription(dto.getDescription());
        interface_.setAuthRequired(dto.getAuthRequired() != null ? dto.getAuthRequired() : false);
        interface_.setServiceId(dto.getServiceId());
        return interface_;
    }
    
    public Interface toEntity(InterfaceUpdateDto dto) {
        Interface interface_ = new Interface();
        interface_.setId(dto.getId());
        interface_.setName(dto.getName());
        interface_.setPath(dto.getPath());
        interface_.setMethod(dto.getMethod());
        interface_.setDescription(dto.getDescription());
        interface_.setAuthRequired(dto.getAuthRequired());
        interface_.setServiceId(dto.getServiceId());
        return interface_;
    }
    
    public InterfaceDetailVo toDetailVo(Interface interface_) {
        InterfaceDetailVo vo = new InterfaceDetailVo();
        vo.setId(interface_.getId());
        vo.setName(interface_.getName());
        vo.setPath(interface_.getPath());
        vo.setMethod(interface_.getMethod());
        vo.setDescription(interface_.getDescription());
        vo.setAuthRequired(interface_.getAuthRequired());
        vo.setStatus(interface_.getStatus());
        vo.setServiceId(interface_.getServiceId());
        vo.setServiceName(interface_.getServiceName());
        vo.setCreatedAt(interface_.getCreatedAt());
        vo.setUpdatedAt(interface_.getUpdatedAt());
        return vo;
    }
    
    public InterfaceListVo toListVo(Interface interface_) {
        InterfaceListVo vo = new InterfaceListVo();
        vo.setId(interface_.getId());
        vo.setName(interface_.getName());
        vo.setPath(interface_.getPath());
        vo.setMethod(interface_.getMethod());
        vo.setAuthRequired(interface_.getAuthRequired());
        vo.setStatus(interface_.getStatus());
        vo.setServiceId(interface_.getServiceId());
        vo.setServiceName(interface_.getServiceName());
        return vo;
    }
}
