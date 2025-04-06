package cloud.xcan.angus.core.gm.interfaces.country.facade.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class CountryDistrictTreeVo extends CountryDistrictDetailVo {

  private List<CountryDistrictTreeVo> children = new ArrayList<>();
}
