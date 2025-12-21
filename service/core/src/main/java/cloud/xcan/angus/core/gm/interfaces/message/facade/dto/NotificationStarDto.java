package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏通知DTO
 */
@Data
@Schema(description = "收藏通知请求")
public class NotificationStarDto {
    
    @Schema(description = "是否收藏", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isStarred;
}
