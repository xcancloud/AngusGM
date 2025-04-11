package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.core.gm.domain.to.TOUserSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class TOUserSearchRepoMySql extends SimpleSearchRepository<TOUser> implements
    TOUserSearchRepo {

}
