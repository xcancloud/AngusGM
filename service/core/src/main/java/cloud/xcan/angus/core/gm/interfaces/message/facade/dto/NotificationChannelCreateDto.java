package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 通知渠道创建DTO
 */
@Data
@Schema(description = "通知渠道创建请求")
public class NotificationChannelCreateDto {
    
    @Schema(description = "渠道名称", example = "New Email Channel", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "渠道名称不能为空")
    private String name;
    
    @Schema(description = "渠道类型", example = "email", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "渠道类型不能为空")
    private String type;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled = true;
    
    @Schema(description = "配置信息")
    private Map<String, String> config;
}
