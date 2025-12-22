package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Group status update response VO
 */
@Data
@Schema(description = "组状态更新响应")
public class GroupStatusUpdateVo {

  @Schema(description = "组ID")
  private Long id;

  @Schema(description = "状态")
  private GroupStatus status;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
