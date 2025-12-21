package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Batch delete authorization DTO
 */
@Data
@Schema(description = "批量删除授权请求参数")
public class AuthorizationBatchDeleteDto {

  @NotEmpty
  @Schema(description = "授权ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> authorizationIds;
}
