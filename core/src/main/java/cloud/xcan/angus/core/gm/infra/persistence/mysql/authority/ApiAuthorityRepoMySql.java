package cloud.xcan.angus.core.gm.infra.persistence.mysql.authority;

import cloud.xcan.angus.core.gm.domain.authority.ApiAuthorityRepo;
import org.springframework.stereotype.Repository;

@Repository("apiAuthorityRepo")
public interface ApiAuthorityRepoMySql extends ApiAuthorityRepo {

}
