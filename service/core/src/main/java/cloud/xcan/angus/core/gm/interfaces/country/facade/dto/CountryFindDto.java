package cloud.xcan.angus.core.gm.interfaces.country.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class CountryFindDto extends PageQuery {

  @Schema(description = "Country identifier")
  private Long id;

  @Schema(description = "Country code for filtering")
  private String code;

  @Schema(description = "Country name for filtering")
  private String name;

  @Schema(description = "Country Chinese name for filtering")
  private String chineseName;

  @Schema(description = "Country ISO 2-letter code for filtering")
  private String iso2;

  @Schema(description = "Country ISO 3-letter code for filtering")
  private String iso3;

  @Schema(description = "Country abbreviation for filtering")
  private String abbr;

  @Schema(description = "Whether country is open for selection")
  private Boolean open;

}
