package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建备份请求参数")
public class BackupCreateDto {
    @NotBlank
    @Schema(description = "备份名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "备份类型（完整备份、增量备份、差异备份）")
    private String type;
    
    @Schema(description = "描述")
    private String description;
}
