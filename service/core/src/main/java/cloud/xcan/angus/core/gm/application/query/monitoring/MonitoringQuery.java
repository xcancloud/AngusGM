package cloud.xcan.angus.core.gm.application.query.monitoring;

import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MonitoringQuery {
    Optional<Monitoring> findById(Long id);
    Page<Monitoring> findAll(Pageable pageable);
    long count();
}
