package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 通知渠道更新DTO
 */
@Data
@Schema(description = "通知渠道更新请求")
public class NotificationChannelUpdateDto {
    
    @Schema(description = "渠道名称", example = "Updated Email Channel")
    private String name;
    
    @Schema(description = "渠道类型", example = "email")
    private String type;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
    
    @Schema(description = "配置信息")
    private Map<String, String> config;
}
