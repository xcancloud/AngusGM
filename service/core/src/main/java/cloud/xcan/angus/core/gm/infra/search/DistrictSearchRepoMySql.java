package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.domain.country.district.DistrictSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DistrictSearchRepoMySql extends SimpleSearchRepository<District> implements
    DistrictSearchRepo {

}
