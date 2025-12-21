package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "密码策略更新DTO")
public class PasswordPolicyUpdateDto {
    
    @Schema(description = "最小长度", example = "10")
    private Integer minLength;
    
    @Schema(description = "最大长度", example = "32")
    private Integer maxLength;
    
    @Schema(description = "是否要求大写字母", example = "true")
    private Boolean requireUppercase;
    
    @Schema(description = "是否要求小写字母", example = "true")
    private Boolean requireLowercase;
    
    @Schema(description = "是否要求数字", example = "true")
    private Boolean requireNumbers;
    
    @Schema(description = "是否要求特殊字符", example = "true")
    private Boolean requireSpecialChars;
    
    @Schema(description = "禁止重复使用次数", example = "6")
    private Integer preventReuse;
    
    @Schema(description = "密码过期天数", example = "60")
    private Integer expirationDays;
    
    @Schema(description = "提前警告天数", example = "7")
    private Integer warningDays;
    
    @Schema(description = "最大登录尝试次数", example = "3")
    private Integer maxLoginAttempts;
    
    @Schema(description = "锁定时长（分钟）", example = "60")
    private Integer lockoutDuration;
}
