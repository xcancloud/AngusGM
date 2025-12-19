package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.dept;


import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserDeptFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long userId;

  @Schema(description = "Department identifier for filtering user-department relationships")
  private Long deptId;

  @Schema(description = "Department name for searching user-department relationships")
  private String deptName;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
