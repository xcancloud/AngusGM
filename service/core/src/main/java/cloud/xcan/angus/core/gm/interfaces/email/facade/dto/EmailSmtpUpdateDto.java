package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新SMTP配置DTO")
public class EmailSmtpUpdateDto {
    
    @NotBlank
    @Schema(description = "SMTP服务器地址", required = true, example = "smtp.exmail.qq.com")
    private String host;
    
    @NotNull
    @Schema(description = "SMTP端口", required = true, example = "465")
    private Integer port;
    
    @NotBlank
    @Schema(description = "用户名", required = true, example = "notify@angusgm.com")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "发件人名称", example = "AngusGM系统")
    private String fromName;
    
    @NotBlank
    @Schema(description = "发件人邮箱", required = true, example = "notify@angusgm.com")
    private String fromEmail;
    
    @Schema(description = "是否使用SSL", example = "true")
    private Boolean useSsl = true;
}
