package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lock/unlock user DTO
 */
@Data
@Schema(description = "锁定/解锁用户请求参数")
public class UserLockDto {

  @NotNull
  @Schema(description = "是否锁定", requiredMode = RequiredMode.REQUIRED)
  private Boolean isLocked;

  @Size(max = 500)
  @Schema(description = "锁定原因")
  private String reason;
}
