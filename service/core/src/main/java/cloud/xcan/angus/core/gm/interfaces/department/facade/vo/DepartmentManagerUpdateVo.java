package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "设置部门负责人响应")
public class DepartmentManagerUpdateVo {

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "负责人ID")
  private Long managerId;

  @Schema(description = "负责人名称")
  private String managerName;

  @Schema(description = "更新时间")
  private LocalDateTime modifiedDate;
}
