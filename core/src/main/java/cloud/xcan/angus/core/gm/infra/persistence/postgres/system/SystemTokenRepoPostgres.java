package cloud.xcan.angus.core.gm.infra.persistence.postgres.system;

import cloud.xcan.angus.core.gm.domain.system.SystemTokenRepo;
import org.springframework.stereotype.Repository;


@Repository("systemTokenRepo")
public interface SystemTokenRepoPostgres extends SystemTokenRepo {

}
