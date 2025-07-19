package cloud.xcan.angus.core.gm.interfaces.country.facade;

import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryDistrictFindDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictDetailVo;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface CountryDistrictFacade {

  List<CountryDistrictTreeVo> tree(String countryCode, String districtCode);

  CountryDistrictDetailVo district(String countryCode, String districtCode);

  List<CountryDistrictDetailVo> province(String countryCode);

  List<CountryDistrictDetailVo> city(String countryCode, String provinceCode);

  List<CountryDistrictDetailVo> areas(String countryCode, String cityCode);

  PageResult<CountryDistrictDetailVo> list(CountryDistrictFindDto dto);
}
