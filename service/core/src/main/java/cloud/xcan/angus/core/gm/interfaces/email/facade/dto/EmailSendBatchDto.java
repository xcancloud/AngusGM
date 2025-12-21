package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "批量发送邮件DTO")
public class EmailSendBatchDto {
    
    @NotEmpty
    @Schema(description = "收件人邮箱列表", required = true)
    private List<String> to;
    
    @NotNull
    @Schema(description = "模板ID", required = true)
    private Long templateId;
    
    @Schema(description = "模板参数")
    private Map<String, String> params;
}
