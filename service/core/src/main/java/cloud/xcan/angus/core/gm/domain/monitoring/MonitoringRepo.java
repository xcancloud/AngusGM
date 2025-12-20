package cloud.xcan.angus.core.gm.domain.monitoring;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 监控仓储接口
 */
public interface MonitoringRepo extends JpaRepository<Monitoring, Long> {

    /**
     * 根据类型查找
     */
    List<Monitoring> findByType(MonitoringType type);

    /**
     * 根据状态查找
     */
    List<Monitoring> findByStatus(MonitoringStatus status);

    /**
     * 根据目标查找
     */
    List<Monitoring> findByTarget(String target);

    /**
     * 根据告警启用状态查找
     */
    Page<Monitoring> findByAlertEnabled(Boolean alertEnabled, Pageable pageable);

    /**
     * 根据类型和状态查找
     */
    List<Monitoring> findByTypeAndStatus(MonitoringType type, MonitoringStatus status);
}
