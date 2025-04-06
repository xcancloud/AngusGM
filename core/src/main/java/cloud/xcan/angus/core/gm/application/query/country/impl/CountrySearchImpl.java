package cloud.xcan.angus.core.gm.application.query.country.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.gm.application.query.country.CountrySearch;
import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.domain.country.CountrySearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class CountrySearchImpl implements CountrySearch {

  @Resource
  private CountrySearchRepo countrySearchRepo;

  @Override
  public Page<Country> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Country> clazz, String... matches) {
    return countrySearchRepo.find(criteria, pageable, clazz, matches);
  }
}
