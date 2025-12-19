package cloud.xcan.angus.core.gm.domain.apimonitoring;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API监控仓储接口
 */
public interface ApiMonitoringRepo extends JpaRepository<ApiMonitoring, Long> {

    /**
     * 根据端点查找
     */
    List<ApiMonitoring> findByEndpoint(String endpoint);

    /**
     * 根据类型查找
     */
    List<ApiMonitoring> findByType(ApiMonitoringType type);

    /**
     * 根据状态查找
     */
    Page<ApiMonitoring> findByStatus(ApiMonitoringStatus status, Pageable pageable);

    /**
     * 根据时间范围查找
     */
    List<ApiMonitoring> findByRequestTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据端点和时间范围查找
     */
    List<ApiMonitoring> findByEndpointAndRequestTimeBetween(String endpoint, LocalDateTime startTime, LocalDateTime endTime);
}
