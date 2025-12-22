package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Group status update DTO
 */
@Data
@Schema(description = "更新组状态请求参数")
public class GroupStatusUpdateDto {

  @NotNull
  @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, 
      allowableValues = {"ENABLED", "DISABLED"}, 
      example = "ENABLED")
  private GroupStatus status;
}
