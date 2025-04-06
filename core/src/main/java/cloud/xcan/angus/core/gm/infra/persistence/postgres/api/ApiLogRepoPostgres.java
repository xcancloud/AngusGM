package cloud.xcan.angus.core.gm.infra.persistence.postgres.api;

import cloud.xcan.angus.core.gm.domain.api.log.ApiLogRepo;
import org.springframework.stereotype.Repository;


@Repository("apiLogRepo")
public interface ApiLogRepoPostgres extends ApiLogRepo {

}
