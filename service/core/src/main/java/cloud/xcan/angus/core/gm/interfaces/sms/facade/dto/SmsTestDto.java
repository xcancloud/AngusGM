package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "测试短信DTO")
public class SmsTestDto {
    
    @NotBlank
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;
    
    @NotBlank
    @Schema(description = "短信内容", required = true, example = "这是一条测试短信")
    private String content;
}
