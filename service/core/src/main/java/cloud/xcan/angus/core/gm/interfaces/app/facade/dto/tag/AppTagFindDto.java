package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppTagFindDto extends PageQuery {

  @Schema(description = "Application tag identifier for filtering")
  private Long id;

  @Schema(description = "Application tag display name for filtering")
  private String name;

}
