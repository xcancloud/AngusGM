package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 归档通知DTO
 */
@Data
@Schema(description = "归档通知请求")
public class NotificationArchiveDto {
    
    @Schema(description = "是否归档", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isArchived;
}
