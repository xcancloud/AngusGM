package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.core.gm.domain.to.TORoleSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class TORoleSearchRepoMySql extends SimpleSearchRepository<TORole> implements
    TORoleSearchRepo {

}
