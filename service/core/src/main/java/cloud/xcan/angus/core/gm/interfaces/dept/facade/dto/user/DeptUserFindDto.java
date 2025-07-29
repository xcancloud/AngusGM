package cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.user;

import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeptUserFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long deptId;

  @Schema(description = "User identifier for filtering")
  private Long userId;

  @Schema(description = "User full name for filtering")
  private String fullName;

  @Schema(description = "User creation date for filtering")
  private LocalDateTime createdDate;

  @Schema(description = "Whether user is department head")
  private Boolean deptHead;

  @Schema(description = "Whether this is user's main department")
  private Boolean mainDept;

}
