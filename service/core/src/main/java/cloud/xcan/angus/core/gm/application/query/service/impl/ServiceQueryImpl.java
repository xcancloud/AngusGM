package cloud.xcan.angus.core.gm.application.query.service.impl;

import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.domain.service.ServiceRepo;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 服务管理查询实现
 */
@Component
@RequiredArgsConstructor
public class ServiceQueryImpl implements ServiceQuery {

    private final ServiceRepo serviceRepo;

    @Override
    public Optional<Service> findById(String id) {
        return serviceRepo.findById(id);
    }

    @Override
    public Page<Service> find(ServiceStatus status, ServiceProtocol protocol,
                              String applicationId, String version, Pageable pageable) {
        return serviceRepo.find(status, protocol, applicationId, version, pageable);
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = serviceRepo.count();
        long enabled = serviceRepo.countByStatus(ServiceStatus.ENABLED);
        long disabled = serviceRepo.countByStatus(ServiceStatus.DISABLED);
        
        stats.put("total", total);
        stats.put("enabled", enabled);
        stats.put("disabled", disabled);
        stats.put("protocolDistribution", serviceRepo.countByProtocol());
        stats.put("versionCount", serviceRepo.countDistinctVersions());
        stats.put("avgInterfaceCount", serviceRepo.avgInterfaceCount());
        
        return stats;
    }
}
