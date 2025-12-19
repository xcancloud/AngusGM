package cloud.xcan.angus.core.gm.infra.persistence.mysql.systemversion;

import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersionRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemVersionRepoMysql extends JpaRepository<SystemVersion, Long>, SystemVersionRepo {
}
