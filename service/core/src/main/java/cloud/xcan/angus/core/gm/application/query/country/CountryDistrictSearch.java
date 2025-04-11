package cloud.xcan.angus.core.gm.application.query.country;

import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CountryDistrictSearch {

  Page<District> search(Set<SearchCriteria> criteria, Pageable pageable, Class<District> bookClass,
      String... matches);
}
