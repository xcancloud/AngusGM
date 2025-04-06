package cloud.xcan.angus.core.gm.interfaces.country.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryDistrictAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryDistrictAssembler.toDistrictVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.query.country.CountryDistrictQuery;
import cloud.xcan.angus.core.gm.application.query.country.CountryDistrictSearch;
import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.interfaces.country.facade.CountryDistrictFacade;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryDistrictSearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.internal.assembler.CountryDistrictAssembler;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictDetailVo;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CountryDistrictFacadeImpl implements CountryDistrictFacade {

  @Resource
  private CountryDistrictQuery districtQuery;

  @Resource
  private CountryDistrictSearch districtSearch;

  @Override
  public List<CountryDistrictTreeVo> tree(String countryCode, String districtCode) {
    return districtQuery.tree(countryCode, districtCode);
  }

  @Override
  public CountryDistrictDetailVo district(String countryCode, String districtCode) {
    return toDistrictVo(districtQuery.detail(countryCode, districtCode));
  }

  @Override
  public List<CountryDistrictDetailVo> province(String countryCode) {
    return districtQuery.province(countryCode).stream()
        .map(CountryDistrictAssembler::toDistrictVo).collect(Collectors.toList());
  }

  @Override
  public List<CountryDistrictDetailVo> city(String countryCode, String provinceCode) {
    return districtQuery.city(countryCode, provinceCode).stream()
        .map(CountryDistrictAssembler::toDistrictVo)
        .collect(Collectors.toList());
  }

  @Override
  public List<CountryDistrictDetailVo> areas(String countryCode, String cityCode) {
    return districtQuery.areas(countryCode, cityCode).stream()
        .map(CountryDistrictAssembler::toDistrictVo)
        .collect(Collectors.toList());
  }

  @Override
  public PageResult<CountryDistrictDetailVo> search(CountryDistrictSearchDto dto) {
    Page<District> bookPage = districtSearch.search(getSearchCriteria(dto), dto.tranPage(),
        District.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(bookPage, CountryDistrictAssembler::toDistrictVo);
  }

}
