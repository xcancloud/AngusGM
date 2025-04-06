package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.core.gm.domain.api.ApiSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ApiSearchRepoMySql extends SimpleSearchRepository<Api> implements ApiSearchRepo {

}
