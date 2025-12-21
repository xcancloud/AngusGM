package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "双因素认证配置更新DTO")
public class TwoFactorConfigUpdateDto {
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
    
    @Schema(description = "认证方式列表")
    private List<String> methods;
    
    @Schema(description = "默认认证方式", example = "totp")
    private String defaultMethod;
    
    @Schema(description = "验证码过期时间（秒）", example = "600")
    private Integer codeExpiration;
    
    @Schema(description = "信任设备天数", example = "30")
    private Integer trustDeviceDays;
    
    @Schema(description = "是否强制管理员使用", example = "true")
    private Boolean enforceForAdmins;
    
    @Schema(description = "是否强制所有用户使用", example = "true")
    private Boolean enforceForAllUsers;
}
