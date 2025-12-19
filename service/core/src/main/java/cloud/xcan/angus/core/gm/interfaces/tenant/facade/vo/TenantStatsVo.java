package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Tenant statistics VO
 */
@Data
@Schema(description = "租户统计数据")
public class TenantStatsVo {

  @Schema(description = "总租户数")
  private Long totalTenants;

  @Schema(description = "已启用租户数")
  private Long enabledTenants;

  @Schema(description = "已禁用租户数")
  private Long disabledTenants;

  @Schema(description = "总用户数")
  private Long totalUsers;

  @Schema(description = "本月新增租户数")
  private Long newTenantsThisMonth;

  @Schema(description = "增长率（%）")
  private Double growthRate;
}
