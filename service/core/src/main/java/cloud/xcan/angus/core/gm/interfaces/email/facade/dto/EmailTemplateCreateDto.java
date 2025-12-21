package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建邮件模板DTO")
public class EmailTemplateCreateDto {
    
    @NotBlank
    @Schema(description = "模板名称", required = true, example = "系统通知邮件")
    private String name;
    
    @NotBlank
    @Schema(description = "模板编码", required = true, example = "SYSTEM_NOTIFY")
    private String code;
    
    @NotBlank
    @Schema(description = "模板类型", required = true, example = "通知邮件")
    private String type;
    
    @NotBlank
    @Schema(description = "邮件主题", required = true, example = "【AngusGM】{event}通知")
    private String subject;
    
    @NotBlank
    @Schema(description = "模板内容", required = true)
    private String content;
    
    @Schema(description = "模板参数")
    private List<String> params;
}
