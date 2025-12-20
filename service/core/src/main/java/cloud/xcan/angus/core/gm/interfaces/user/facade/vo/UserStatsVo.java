package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * User statistics VO
 */
@Data
@Schema(description = "用户统计数据")
public class UserStatsVo {

  @Schema(description = "总用户数")
  private Long totalUsers;

  @Schema(description = "已激活用户数")
  private Long activeUsers;

  @Schema(description = "已禁用用户数")
  private Long disabledUsers;

  @Schema(description = "待审核用户数")
  private Long pendingUsers;

  @Schema(description = "在线用户数")
  private Long onlineUsers;

  @Schema(description = "本月新增用户数")
  private Long newUsersThisMonth;
}
