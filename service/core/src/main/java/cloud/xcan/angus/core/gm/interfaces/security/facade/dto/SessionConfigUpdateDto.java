package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会话配置更新DTO")
public class SessionConfigUpdateDto {
    
    @Schema(description = "超时时间（秒）", example = "3600")
    private Integer timeout;
    
    @Schema(description = "最大并发会话数", example = "1")
    private Integer maxConcurrent;
    
    @Schema(description = "是否允许多设备登录", example = "false")
    private Boolean allowMultipleDevices;
    
    @Schema(description = "是否启用自动注销", example = "true")
    private Boolean autoLogoutEnabled;
    
    @Schema(description = "记住我天数", example = "7")
    private Integer rememberMeDays;
}
