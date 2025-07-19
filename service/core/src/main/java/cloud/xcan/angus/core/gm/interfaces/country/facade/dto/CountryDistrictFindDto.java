package cloud.xcan.angus.core.gm.interfaces.country.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class CountryDistrictFindDto extends PageQuery {

  private String code;

  private String name;

  private String pinYin;

}
