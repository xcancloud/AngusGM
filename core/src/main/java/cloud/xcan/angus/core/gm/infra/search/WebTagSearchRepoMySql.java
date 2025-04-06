package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.gm.domain.tag.WebTagSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class WebTagSearchRepoMySql extends SimpleSearchRepository<WebTag> implements
    WebTagSearchRepo {

}
