package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Authorization Update DTO
 */
@Data
@Schema(description = "更新授权请求参数")
public class AuthorizationUpdateDto {
    
    @NotEmpty
    @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;
}
