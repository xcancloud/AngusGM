package cloud.xcan.angus.core.gm.infra.persistence.postgres.auditlog;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLogRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepoPostgres extends JpaRepository<AuditLog, Long>, AuditLogRepo {
}
