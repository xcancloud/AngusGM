package cloud.xcan.angus.core.gm.infra.persistence.mysql.service;

import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.domain.service.ServiceRepo;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import cloud.xcan.angus.infra.persistence.mysql.BaseRepositoryMysql;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * 服务仓储MySQL实现
 */
@Repository
public class ServiceRepoMysql extends BaseRepositoryMysql<Service, String> implements ServiceRepo {

    @Override
    public Optional<Service> findByCode(String code) {
        return findOne((root, query, cb) -> cb.equal(root.get("code"), code));
    }

    @Override
    public Page<Service> find(ServiceStatus status, ServiceProtocol protocol,
                              String applicationId, String version, Pageable pageable) {
        return findAll((root, query, cb) -> {
            var predicates = cb.conjunction();
            
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            if (protocol != null) {
                predicates = cb.and(predicates, cb.equal(root.get("protocol"), protocol));
            }
            if (applicationId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("applicationId"), applicationId));
            }
            if (version != null) {
                predicates = cb.and(predicates, cb.equal(root.get("version"), version));
            }
            
            return predicates;
        }, pageable);
    }

    @Override
    public long countByStatus(ServiceStatus status) {
        return count((root, query, cb) -> cb.equal(root.get("status"), status));
    }

    @Override
    public Map<ServiceProtocol, Long> countByProtocol() {
        // Simplified implementation - in production would use native query
        return Map.of(
                ServiceProtocol.HTTP, count((root, query, cb) -> cb.equal(root.get("protocol"), ServiceProtocol.HTTP)),
                ServiceProtocol.HTTPS, count((root, query, cb) -> cb.equal(root.get("protocol"), ServiceProtocol.HTTPS)),
                ServiceProtocol.GRPC, count((root, query, cb) -> cb.equal(root.get("protocol"), ServiceProtocol.GRPC)),
                ServiceProtocol.WEBSOCKET, count((root, query, cb) -> cb.equal(root.get("protocol"), ServiceProtocol.WEBSOCKET)),
                ServiceProtocol.TCP, count((root, query, cb) -> cb.equal(root.get("protocol"), ServiceProtocol.TCP))
        );
    }

    @Override
    public long countDistinctVersions() {
        // Simplified implementation
        return findAll().stream()
                .map(Service::getVersion)
                .distinct()
                .count();
    }

    @Override
    public double avgInterfaceCount() {
        return findAll().stream()
                .mapToInt(Service::getInterfaceCount)
                .average()
                .orElse(0.0);
    }
}
