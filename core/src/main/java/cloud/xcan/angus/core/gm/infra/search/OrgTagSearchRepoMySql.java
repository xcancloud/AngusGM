package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.core.gm.domain.tag.OrgTagSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class OrgTagSearchRepoMySql extends SimpleSearchRepository<OrgTag> implements
    OrgTagSearchRepo {


}
