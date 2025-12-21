package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新邮件模板DTO")
public class EmailTemplateUpdateDto {
    
    @NotBlank
    @Schema(description = "模板名称", required = true)
    private String name;
    
    @NotBlank
    @Schema(description = "模板编码", required = true)
    private String code;
    
    @NotBlank
    @Schema(description = "模板类型", required = true)
    private String type;
    
    @NotBlank
    @Schema(description = "邮件主题", required = true)
    private String subject;
    
    @NotBlank
    @Schema(description = "模板内容", required = true)
    private String content;
    
    @Schema(description = "模板参数")
    private List<String> params;
}
