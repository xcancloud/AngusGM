package cloud.xcan.angus.core.gm.infra.persistence.postgres.email;

import cloud.xcan.angus.core.gm.domain.email.server.EmailServerRepo;
import org.springframework.stereotype.Repository;

@Repository("serverRepo")
public interface EmailServerRepoPostgres extends EmailServerRepo {

}
