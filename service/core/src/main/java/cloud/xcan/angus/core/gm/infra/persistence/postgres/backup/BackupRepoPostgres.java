package cloud.xcan.angus.core.gm.infra.persistence.postgres.backup;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupRepoPostgres extends BackupRepo, JpaRepository<Backup, Long> {
}
