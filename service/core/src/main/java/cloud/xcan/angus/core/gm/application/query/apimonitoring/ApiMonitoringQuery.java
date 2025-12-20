package cloud.xcan.angus.core.gm.application.query.apimonitoring;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApiMonitoringQuery {
    Optional<ApiMonitoring> findById(Long id);
    Page<ApiMonitoring> findAll(Pageable pageable);
    long count();
}
