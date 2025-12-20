package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * Update group DTO
 */
@Data
@Schema(description = "更新组请求参数")
public class GroupUpdateDto {

  @Size(max = 100)
  @Schema(description = "组名称")
  private String name;

  @Size(max = 500)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "组类型")
  private GroupType type;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "成员ID列表")
  private List<Long> memberIds;

  @Schema(description = "状态")
  private GroupStatus status;
}
