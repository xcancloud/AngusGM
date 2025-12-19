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

  @Schema(description = "根部门数")
  private Long rootDepartments;

  @Schema(description = "平均层级深度")
  private Double averageLevel;

  @Schema(description = "最大层级深度")
  private Integer maxLevel;
}
