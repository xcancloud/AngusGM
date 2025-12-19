package cloud.xcan.angus.core.gm.infra.persistence.mysql.apimonitoring;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiMonitoringRepoMysql extends JpaRepository<ApiMonitoring, Long>, ApiMonitoringRepo {
}
