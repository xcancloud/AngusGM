package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "发送短信DTO")
public class SmsSendDto {
    
    @NotBlank
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;
    
    @NotNull
    @Schema(description = "模板ID", required = true)
    private Long templateId;
    
    @Schema(description = "模板参数")
    private Map<String, String> params;
}
