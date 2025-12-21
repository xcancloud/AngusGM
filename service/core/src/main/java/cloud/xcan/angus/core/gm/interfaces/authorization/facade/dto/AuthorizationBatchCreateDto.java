package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * Batch create authorization DTO
 */
@Data
@Schema(description = "批量授权请求参数")
public class AuthorizationBatchCreateDto {

  @NotBlank
  @Schema(description = "目标类型（user、department、group）")
  private String targetType;

  @NotEmpty
  @Schema(description = "目标ID列表")
  private List<String> targetIds;

  @NotEmpty
  @Schema(description = "角色ID列表")
  private List<String> roleIds;
}
