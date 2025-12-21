package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Department statistics VO
 */
@Data
@Schema(description = "部门统计数据")
public class DepartmentStatsVo {

  @Schema(description = "总部门数")
  private Long totalDepartments;

  @Schema(description = "已启用部门数")
  private Long enabledDepartments;

  @Schema(description = "已禁用部门数")
  private Long disabledDepartments;

  @Schema(description = "总用户数")
  private Long totalUsers;

  @Schema(description = "最大层级深度")
  private Integer maxLevel;

  @Schema(description = "本月新增部门数")
  private Long newDepartmentsThisMonth;
}
