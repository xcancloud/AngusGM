package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "邮件模板状态DTO")
public class EmailTemplateStatusDto {
    
    @NotBlank
    @Schema(description = "状态", required = true, example = "已禁用")
    private String status;
}
