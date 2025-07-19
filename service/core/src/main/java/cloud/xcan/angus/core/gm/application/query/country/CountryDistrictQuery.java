package cloud.xcan.angus.core.gm.application.query.country;

import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CountryDistrictQuery {

  District detail(String country, String district);

  Page<District> list(GenericSpecification<District> spec, PageRequest pageRequest,
      boolean fullTextSearch, String[] match);

  List<District> province(String countryCode);

  List<District> city(String countryCode, String provinceCode);

  List<District> areas(String countryCode, String code);

  District find(Long id);

  List<CountryDistrictTreeVo> tree(String countryCode, String districtCode);

}
