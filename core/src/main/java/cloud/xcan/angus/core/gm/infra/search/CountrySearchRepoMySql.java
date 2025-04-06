package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.domain.country.CountrySearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class CountrySearchRepoMySql extends SimpleSearchRepository<Country> implements
    CountrySearchRepo {

}
