package cloud.xcan.angus.core.gm.application.query.country.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.country.CountryQuery;
import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.domain.country.CountryRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class CountryQueryImpl implements CountryQuery {

  @Resource
  private CountryRepo countryRepo;

  @Override
  public Country find(Long id) {
    return new BizTemplate<Country>() {

      @Override
      protected Country process() {
        return countryRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "Country"));
      }
    }.execute();
  }

  @Override
  public Page<Country> find(Specification<Country> spec, Pageable pageable) {
    return new BizTemplate<Page<Country>>() {

      @Override
      protected Page<Country> process() {
        return countryRepo.findAll(spec, pageable);
      }
    }.execute();
  }

}
