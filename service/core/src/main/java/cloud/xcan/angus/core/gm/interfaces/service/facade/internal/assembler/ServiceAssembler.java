package cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 服务装配器
 */
@Component
public class ServiceAssembler {

    public Service toEntity(ServiceCreateDto dto) {
        Service service = new Service();
        service.setName(dto.getName());
        service.setCode(dto.getCode());
        service.setDescription(dto.getDescription());
        service.setProtocol(dto.getProtocol());
        service.setVersion(dto.getVersion());
        service.setBaseUrl(dto.getBaseUrl());
        service.setApplicationId(dto.getApplicationId());
        return service;
    }

    public Service toEntity(ServiceUpdateDto dto) {
        Service service = new Service();
        service.setId(dto.getId());
        service.setName(dto.getName());
        service.setCode(dto.getCode());
        service.setDescription(dto.getDescription());
        service.setProtocol(dto.getProtocol());
        service.setVersion(dto.getVersion());
        service.setBaseUrl(dto.getBaseUrl());
        service.setApplicationId(dto.getApplicationId());
        return service;
    }

    public ServiceDetailVo toDetailVo(Service service) {
        ServiceDetailVo vo = new ServiceDetailVo();
        vo.setId(service.getId());
        vo.setName(service.getName());
        vo.setCode(service.getCode());
        vo.setDescription(service.getDescription());
        vo.setStatus(service.getStatus());
        vo.setProtocol(service.getProtocol());
        vo.setVersion(service.getVersion());
        vo.setBaseUrl(service.getBaseUrl());
        vo.setApplicationId(service.getApplicationId());
        vo.setApplicationName(service.getApplicationName());
        vo.setInterfaceCount(service.getInterfaceCount());
        vo.setCreatedAt(service.getCreatedAt());
        vo.setUpdatedAt(service.getUpdatedAt());
        return vo;
    }

    public ServiceListVo toListVo(Service service) {
        ServiceListVo vo = new ServiceListVo();
        vo.setId(service.getId());
        vo.setName(service.getName());
        vo.setCode(service.getCode());
        vo.setStatus(service.getStatus());
        vo.setProtocol(service.getProtocol());
        vo.setVersion(service.getVersion());
        vo.setApplicationId(service.getApplicationId());
        vo.setApplicationName(service.getApplicationName());
        vo.setInterfaceCount(service.getInterfaceCount());
        return vo;
    }

    public ServiceStatsVo toStatsVo(Map<String, Object> stats) {
        ServiceStatsVo vo = new ServiceStatsVo();
        vo.setTotal((Long) stats.get("total"));
        vo.setEnabled((Long) stats.get("enabled"));
        vo.setDisabled((Long) stats.get("disabled"));
        vo.setProtocolDistribution((Map<String, Long>) stats.get("protocolDistribution"));
        vo.setVersionCount((Long) stats.get("versionCount"));
        vo.setAvgInterfaceCount((Double) stats.get("avgInterfaceCount"));
        return vo;
    }
}
