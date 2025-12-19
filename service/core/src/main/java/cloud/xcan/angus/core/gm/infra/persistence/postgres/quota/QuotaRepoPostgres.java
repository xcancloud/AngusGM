package cloud.xcan.angus.core.gm.infra.persistence.postgres.quota;

import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.domain.quota.QuotaRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaRepoPostgres extends QuotaRepo, JpaRepository<Quota, Long> {
}
