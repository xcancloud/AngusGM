package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * Group member remove DTO
 */
@Data
@Schema(description = "批量移除组成员请求参数")
public class GroupMemberRemoveDto {

  @NotEmpty
  @Schema(description = "用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> userIds;
}
