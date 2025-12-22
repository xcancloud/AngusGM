package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Group owner update DTO
 */
@Data
@Schema(description = "更新组负责人请求参数")
public class GroupOwnerUpdateDto {

  @NotNull
  @Schema(description = "负责人ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long ownerId;
}
