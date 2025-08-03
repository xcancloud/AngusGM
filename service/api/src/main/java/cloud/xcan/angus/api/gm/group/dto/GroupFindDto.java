package cloud.xcan.angus.api.gm.group.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class GroupFindDto extends PageQuery {

  @Schema(description = "Unique identifier for the group. Used for precise group identification and lookup")
  private Long id;

  @Schema(description = "Group name for display and identification. Used for group search and filtering")
  private String name;

  @Schema(description = "Group code for identification and reference. Used for group lookup and organization")
  private String code;

  @Schema(description = "Flag indicating whether the group is enabled or disabled. Used for group lifecycle management")
  private Boolean enabled;

  @Schema(description = "User identifier who created the group. Used for audit and tracking purposes")
  private Long createdBy;

  @Schema(description = "Timestamp when the group was created. Used for audit and lifecycle tracking")
  private LocalDateTime createdDate;

  @Schema(description = "Group association tag identifier for categorization and grouping")
  private Long tagId;
}
