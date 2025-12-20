package cloud.xcan.angus.core.gm.infra.persistence.postgres.monitoring;

import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;
import cloud.xcan.angus.core.gm.domain.monitoring.MonitoringRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringRepoPostgres extends JpaRepository<Monitoring, Long>, MonitoringRepo {
}
