package cloud.xcan.angus.core.gm.interfaces.country.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CountrySearchDto extends PageQuery {

  private Long id;

  private String code;

  private String name;

  private String chineseName;

  private String iso2;

  private String iso3;

  private String abbr;

  private Boolean open;

}
