package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * Batch delete users DTO
 */
@Data
@Schema(description = "批量删除用户请求参数")
public class UserBatchDeleteDto {

  @NotEmpty
  @Schema(description = "用户ID列表", requiredMode = RequiredMode.REQUIRED)
  private List<Long> userIds;
}
