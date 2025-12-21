package cloud.xcan.angus.core.gm.interfaces.authenticationorization.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * Batch delete authorization DTO
 */
@Data
@Schema(description = "批量删除授权请求参数")
public class AuthorizationBatchDeleteDto {

  @NotEmpty
  @Schema(description = "授权ID列表")
  private List<String> authorizationIds;
}
