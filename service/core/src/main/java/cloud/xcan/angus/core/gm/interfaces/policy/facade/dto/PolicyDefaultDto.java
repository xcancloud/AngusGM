package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Policy default setting DTO
 */
@Data
@Schema(description = "设置默认角色请求参数")
public class PolicyDefaultDto {

  @Schema(description = "是否设为默认角色")
  private Boolean isDefault;
}
