package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 通知规则创建DTO
 */
@Data
@Schema(description = "通知规则创建请求")
public class NotificationRuleCreateDto {
    
    @Schema(description = "规则名称", example = "用户登录失败告警", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规则名称不能为空")
    private String name;
    
    @Schema(description = "事件类型", example = "user.login.failed", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "事件类型不能为空")
    private String event;
    
    @Schema(description = "渠道ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "渠道列表不能为空")
    private List<String> channels;
    
    @Schema(description = "条件表达式", example = "attempts >= 3")
    private String conditions;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled = true;
}
