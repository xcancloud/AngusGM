package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * Add roles to authorization DTO
 */
@Data
@Schema(description = "添加角色请求参数")
public class AuthorizationRoleAddDto {

  @NotEmpty
  @Schema(description = "角色ID列表")
  private List<Long> roleIds;
}
