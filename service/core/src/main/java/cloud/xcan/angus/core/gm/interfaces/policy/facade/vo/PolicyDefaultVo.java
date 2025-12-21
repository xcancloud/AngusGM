package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Policy default setting VO
 */
@Data
@Schema(description = "设置默认角色响应")
public class PolicyDefaultVo {

  @Schema(description = "角色ID")
  private Long id;

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "是否默认角色")
  private Boolean isDefault;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
