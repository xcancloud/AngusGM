package cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler;


import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryDistrictSearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictDetailVo;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class CountryDistrictAssembler {

  public static CountryDistrictDetailVo toDistrictVo(District district) {
    return new CountryDistrictDetailVo().setCode(district.getCode())
        .setCityCode(district.getCityCode())
        .setCountryCode(district.getCountryCode())
        .setCountryName(district.getCountryName())
        .setId(district.getId())
        .setLat(district.getLat())
        .setLevel(district.getLevel())
        .setLng(district.getLng())
        .setMerName(district.getMerName())
        .setParentCode(district.getParentCode())
        .setName(district.getName())
        .setPinYin(district.getPinYin())
        .setSimpleName(district.getSimpleName())
        .setZipCode(district.getZipCode());
    //  .setGeoData(CountryDistrictAssembler.geoDataToVo(district.getGeoData()));
  }


  public static Set<SearchCriteria> getSearchCriteria(CountryDistrictSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id")
        .matchSearchFields("name", "code", "pinYin")
        .orderByFields("id", "code")
        .build();
  }
}
