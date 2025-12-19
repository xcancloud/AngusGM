package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class WebTagFindDto extends PageQuery {

  @Schema(description = "Web application tag unique identifier for filtering")
  private Long id;

  @Schema(description = "Web application tag display name for searching")
  private String name;

}
