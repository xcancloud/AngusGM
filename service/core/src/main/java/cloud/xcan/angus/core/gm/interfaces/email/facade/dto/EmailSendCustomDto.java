package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "发送自定义邮件DTO")
public class EmailSendCustomDto {
    
    @NotBlank
    @Schema(description = "收件人邮箱", required = true, example = "user@example.com")
    private String to;
    
    @Schema(description = "抄送邮箱")
    private String cc;
    
    @Schema(description = "密送邮箱")
    private String bcc;
    
    @NotBlank
    @Schema(description = "邮件主题", required = true, example = "重要通知")
    private String subject;
    
    @NotBlank
    @Schema(description = "邮件内容", required = true)
    private String content;
    
    @Schema(description = "内容类型", example = "html")
    private String contentType = "html";
    
    @Schema(description = "附件列表")
    private List<EmailAttachment> attachments;
    
    @Data
    @Schema(description = "邮件附件")
    public static class EmailAttachment {
        
        @Schema(description = "文件名")
        private String fileName;
        
        @Schema(description = "文件URL")
        private String fileUrl;
    }
}
