package cloud.xcan.angus.core.gm.application.query.country.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.gm.application.query.country.CountryDistrictSearch;
import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.domain.country.district.DistrictSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class CountryDistrictSearchImpl implements CountryDistrictSearch {

  @Resource
  private DistrictSearchRepo districtSearchRepo;

  @Override
  public Page<District> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<District> districtClass, String... matches) {
    return districtSearchRepo.find(criteria, pageable, districtClass, matches);
  }
}
