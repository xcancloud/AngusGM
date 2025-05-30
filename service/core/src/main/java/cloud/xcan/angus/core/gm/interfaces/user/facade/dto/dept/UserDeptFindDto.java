package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.dept;


import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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

  private Long deptId;

  private String deptName;

  private Long createdBy;

  private LocalDateTime createdDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
