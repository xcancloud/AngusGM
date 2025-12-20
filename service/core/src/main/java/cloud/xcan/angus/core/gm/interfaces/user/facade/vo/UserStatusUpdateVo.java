package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * User status update VO
 */
@Data
@Schema(description = "用户状态更新响应")
public class UserStatusUpdateVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "启用状态")
  private EnableStatus enableStatus;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
