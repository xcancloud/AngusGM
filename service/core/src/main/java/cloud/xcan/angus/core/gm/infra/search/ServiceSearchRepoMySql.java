package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.gm.domain.service.ServiceSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class ServiceSearchRepoMySql extends SimpleSearchRepository<Service> implements
    ServiceSearchRepo {

}
