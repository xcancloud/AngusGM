package cloud.xcan.angus.core.gm.application.query.country;

import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CountrySearch {

  Page<Country> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Country> clazz,
      String... matches);
}
