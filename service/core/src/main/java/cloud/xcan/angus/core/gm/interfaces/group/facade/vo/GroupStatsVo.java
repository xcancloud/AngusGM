package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Group statistics VO
 */
@Data
@Schema(description = "组统计数据")
public class GroupStatsVo {

  @Schema(description = "总组数")
  private Long totalGroups;

  @Schema(description = "已启用组数")
  private Long enabledGroups;

  @Schema(description = "已禁用组数")
  private Long disabledGroups;

  @Schema(description = "系统组数")
  private Long systemGroups;

  @Schema(description = "自定义组数")
  private Long customGroups;

  @Schema(description = "平均成员数")
  private Double averageMemberCount;
}
