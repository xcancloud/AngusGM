package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.core.gm.domain.to.TOPolicySearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class TOPolicySearchRepoMySql extends SimpleSearchRepository<TORole> implements
    TOPolicySearchRepo {

}
