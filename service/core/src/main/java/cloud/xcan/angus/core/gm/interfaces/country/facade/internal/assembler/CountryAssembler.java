package cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler;


import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryFindDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountrySearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class CountryAssembler {

  public static CountryDetailVo toCountryVo(Country country) {
    return new CountryDetailVo().setAbbr(country.getAbbr())
        .setCode(country.getCode())
        .setId(country.getId())
        .setChineseName(country.getChineseName())
        .setIso2(country.getIso2())
        .setIso3(country.getIso3())
        .setOpen(country.getOpen())
        .setName(country.getName());
  }

  public static Specification<Country> getSpecification(CountryFindDto findDto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(findDto)
        .matchSearchFields("code", "name", "chineseName", "iso2", "iso3", "abbr")
        .rangeSearchFields("id")
        .orderByFields("id", "code")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(CountrySearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name", "chineseName")
        .orderByFields("id", "createdDate")
        .build();
  }
}
