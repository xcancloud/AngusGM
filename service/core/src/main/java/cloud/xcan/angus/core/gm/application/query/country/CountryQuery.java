package cloud.xcan.angus.core.gm.application.query.country;

import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CountryQuery {

  Country detail(Long id);

  Page<Country> list(GenericSpecification<Country> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);
}
