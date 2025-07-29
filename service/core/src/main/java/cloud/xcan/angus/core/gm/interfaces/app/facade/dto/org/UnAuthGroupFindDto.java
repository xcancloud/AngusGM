package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class UnAuthGroupFindDto extends PageQuery {

  @Schema(description = "Unauthorized group identifier for filtering")
  private Long id;

  @Schema(description = "Unauthorized group display name for filtering")
  private String name;

}
