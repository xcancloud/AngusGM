package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建短信模板DTO")
public class SmsTemplateCreateDto {
    
    @NotBlank
    @Schema(description = "模板名称", required = true, example = "系统通知模板")
    private String name;
    
    @NotBlank
    @Schema(description = "模板编码", required = true, example = "SYSTEM_NOTIFY")
    private String code;
    
    @NotBlank
    @Schema(description = "模板类型", required = true, example = "通知")
    private String type;
    
    @NotBlank
    @Schema(description = "模板内容", required = true, example = "【AngusGM】尊敬的{name}，{event}将于{time}进行，请知悉。")
    private String content;
    
    @Schema(description = "模板参数")
    private List<String> params;
    
    @Schema(description = "服务商", example = "阿里云")
    private String provider;
    
    @Schema(description = "服务商模板编码", example = "SMS_987654321")
    private String templateCode;
}
