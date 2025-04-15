package cloud.xcan.angus.api.gm.dept.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeptSearchDto extends PageQuery {

  @Schema(description = "Department id.")
  private Long id;

  @Schema(description = "Department code.")
  private String code;

  @Schema(description = "Department name.")
  private String name;

  @Schema(description = "Parent department id.")
  private Long pid;

  @Schema(description = "Department level.")
  private Integer level;

  @Schema(description = "Department association tag id.")
  private Long tagId;

  @Schema(description = "Department creation user id.")
  private Long createdBy;

  @Schema(description = "Department creation date.")
  private LocalDateTime createdDate;
}
