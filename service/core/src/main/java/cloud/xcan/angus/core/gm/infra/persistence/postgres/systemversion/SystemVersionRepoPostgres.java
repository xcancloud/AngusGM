package cloud.xcan.angus.core.gm.infra.persistence.postgres.systemversion;

import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersionRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemVersionRepoPostgres extends JpaRepository<SystemVersion, Long>, SystemVersionRepo {
}
