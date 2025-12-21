package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "创建服务商配置DTO")
public class SmsProviderCreateDto {
    
    @NotBlank
    @Schema(description = "服务商名称", required = true, example = "腾讯云短信")
    private String name;
    
    @NotBlank
    @Schema(description = "服务商编码", required = true, example = "TENCENT")
    private String code;
    
    @Schema(description = "是否默认", example = "false")
    private Boolean isDefault = false;
    
    @NotNull
    @Schema(description = "配置信息", required = true)
    private Map<String, String> config;
}
