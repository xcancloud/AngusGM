package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Update user enable status DTO
 */
@Data
@Schema(description = "更新用户启用状态请求参数")
public class UserStatusUpdateDto {

  @NotNull
  @Schema(description = "启用状态", requiredMode = RequiredMode.REQUIRED)
  private EnableStatus enableStatus;
}
