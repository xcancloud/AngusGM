package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TOUserFindDto extends PageQuery {

  @Schema(description = "Operational user account identifier for filtering")
  private Long userId;

}
