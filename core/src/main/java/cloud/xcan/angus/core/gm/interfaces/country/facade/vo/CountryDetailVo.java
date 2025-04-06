package cloud.xcan.angus.core.gm.interfaces.country.facade.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class CountryDetailVo implements Serializable {

  private Long id;

  private String code;

  private String name;

  private String chineseName;

  private String iso2;

  private String iso3;

  private String abbr;

  private Boolean open;

}
