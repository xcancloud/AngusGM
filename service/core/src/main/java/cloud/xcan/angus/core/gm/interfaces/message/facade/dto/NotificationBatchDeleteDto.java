package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量删除通知DTO
 */
@Data
@Schema(description = "批量删除通知请求")
public class NotificationBatchDeleteDto {
    
    @Schema(description = "通知ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "通知ID列表不能为空")
    private List<String> notificationIds;
}
