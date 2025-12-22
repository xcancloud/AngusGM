package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Group owner update response VO
 */
@Data
@Schema(description = "组负责人更新响应")
public class GroupOwnerUpdateVo {

  @Schema(description = "组ID")
  private Long groupId;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "负责人姓名")
  private String ownerName;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
