package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Role statistics VO
 */
@Data
@Schema(description = "角色统计信息")
public class PolicyStatsVo {

  @Schema(description = "角色总数")
  private Long totalRoles;

  @Schema(description = "系统角色数")
  private Long systemRoles;

  @Schema(description = "自定义角色数")
  private Long customRoles;

  @Schema(description = "用户总数")
  private Long totalUsers;
}
