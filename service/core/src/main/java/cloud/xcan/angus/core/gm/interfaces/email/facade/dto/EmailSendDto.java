package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "发送邮件DTO")
public class EmailSendDto {
    
    @NotBlank
    @Schema(description = "收件人邮箱", required = true, example = "user@example.com")
    private String to;
    
    @Schema(description = "抄送邮箱")
    private String cc;
    
    @Schema(description = "密送邮箱")
    private String bcc;
    
    @NotNull
    @Schema(description = "模板ID", required = true)
    private Long templateId;
    
    @Schema(description = "模板参数")
    private Map<String, String> params;
    
    @Schema(description = "附件列表")
    private List<String> attachments;
}
