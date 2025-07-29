package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;


import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TORoleFindDto extends PageQuery {

  @Schema(description = "Operational role unique identifier for filtering")
  private Long id;

  @Schema(description = "Operational role display name for searching")
  private String name;

  @Schema(description = "Operational role identifier code for filtering")
  private String code;

  @Schema(description = "Associated application identifier for filtering")
  private Long appId;

  @Schema(description = "Operational role activation status for filtering")
  private Boolean enabled;

}
