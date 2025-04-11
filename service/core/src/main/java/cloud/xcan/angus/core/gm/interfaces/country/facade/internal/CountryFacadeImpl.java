package cloud.xcan.angus.core.gm.interfaces.country.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryAssembler.toCountryVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.query.country.CountryQuery;
import cloud.xcan.angus.core.gm.application.query.country.CountrySearch;
import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.interfaces.country.facade.CountryFacade;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryFindDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountrySearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryAssembler;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDetailVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CountryFacadeImpl implements CountryFacade {

  @Resource
  private CountryQuery countryQuery;

  @Resource
  private CountrySearch countrySearch;

  @Override
  public CountryDetailVo detail(Long id) {
    Country country = countryQuery.find(id);
    return toCountryVo(country);
  }

  @Override
  public PageResult<CountryDetailVo> list(CountryFindDto dto) {
    Page<Country> countries = countryQuery.find(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(countries, CountryAssembler::toCountryVo);
  }

  @Override
  public PageResult<CountryDetailVo> search(CountrySearchDto dto) {
    Page<Country> page = countrySearch.search(getSearchCriteria(dto),
        dto.tranPage(), Country.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, CountryAssembler::toCountryVo);
  }

}
