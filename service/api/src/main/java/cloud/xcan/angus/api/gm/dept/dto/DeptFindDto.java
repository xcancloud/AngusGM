package cloud.xcan.angus.api.gm.dept.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeptFindDto extends PageQuery {

  @Schema(description = "Unique identifier for the department. Used for precise department identification")
  private Long id;

  @Schema(description = "Department code for identification and reference. Used for department lookup and organization")
  private String code;

  @Schema(description = "Department name for display and identification. Used for department search and filtering")
  private String name;

  @Schema(description = "Parent department identifier for hierarchical organization structure")
  private Long pid;

  @Schema(description = "Department level in the organizational hierarchy. Used for tree structure management")
  private Integer level;

  @Schema(description = "Department association tag identifier for categorization and grouping")
  private Long tagId;

}
