package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Tag statistics VO
 */
@Data
@Schema(description = "标签统计数据")
public class TagStatsVo {

  @Schema(description = "总标签数")
  private Long totalTags;

  @Schema(description = "已启用标签数")
  private Long enabledTags;

  @Schema(description = "已禁用标签数")
  private Long disabledTags;

  @Schema(description = "分类数量")
  private Long categoryCount;

  @Schema(description = "总使用次数")
  private Long totalUsageCount;
}
