package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Authorization Create DTO
 */
@Data
@Schema(description = "创建授权请求参数")
public class AuthorizationCreateDto {
    
    @NotBlank
    @Schema(description = "目标类型（user、department、group）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetType;
    
    @jakarta.validation.constraints.NotNull
    @Schema(description = "目标ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetId;
    
    @NotEmpty
    @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;
}
