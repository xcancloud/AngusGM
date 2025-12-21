package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "测试SMTP连接DTO")
public class EmailSmtpTestDto {
    
    @NotBlank
    @Schema(description = "SMTP服务器地址", required = true, example = "smtp.exmail.qq.com")
    private String host;
    
    @NotNull
    @Schema(description = "SMTP端口", required = true, example = "465")
    private Integer port;
    
    @NotBlank
    @Schema(description = "用户名", required = true, example = "notify@angusgm.com")
    private String username;
    
    @NotBlank
    @Schema(description = "密码", required = true)
    private String password;
    
    @Schema(description = "是否使用SSL", example = "true")
    private Boolean useSsl = true;
}
