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
public class OrgTagFindDto extends PageQuery {

  @Schema(description = "Organizational tag unique identifier for filtering")
  private Long id;

  @Schema(description = "Organizational tag display name for searching")
  private String name;

  @Schema(description = "Organizational tag creation date for filtering")
  private LocalDateTime createdDate;

}
