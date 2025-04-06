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

  private Long userId;

  private String fullname;

  private LocalDateTime createdDate;

  private Boolean deptHead;

  private Boolean mainDept;

}
