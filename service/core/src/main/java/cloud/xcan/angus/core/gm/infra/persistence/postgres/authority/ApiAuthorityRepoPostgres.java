package cloud.xcan.angus.core.gm.infra.persistence.postgres.authority;

import cloud.xcan.angus.core.gm.domain.authority.ApiAuthorityRepo;
import org.springframework.stereotype.Repository;

@Repository("apiAuthorityRepo")
public interface ApiAuthorityRepoPostgres extends ApiAuthorityRepo {

}
