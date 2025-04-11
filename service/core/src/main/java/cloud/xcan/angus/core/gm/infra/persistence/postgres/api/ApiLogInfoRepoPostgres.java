package cloud.xcan.angus.core.gm.infra.persistence.postgres.api;

import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfoRepo;
import org.springframework.stereotype.Repository;


@Repository("apiLogInfoRepo")
public interface ApiLogInfoRepoPostgres extends ApiLogInfoRepo {

}
