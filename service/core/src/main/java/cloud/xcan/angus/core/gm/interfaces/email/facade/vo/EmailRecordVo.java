package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "邮件记录VO")
public class EmailRecordVo extends TenantAuditingVo {
    
    @Schema(description = "邮件记录ID")
    private Long id;
    
    @Schema(description = "收件人邮箱")
    private String to;
    
    @Schema(description = "抄送邮箱")
    private String cc;
    
    @Schema(description = "密送邮箱")
    private String bcc;
    
    @Schema(description = "邮件主题")
    private String subject;
    
    @Schema(description = "邮件内容")
    private String content;
    
    @Schema(description = "模板ID")
    private Long templateId;
    
    @Schema(description = "模板名称")
    private String templateName;
    
    @Schema(description = "发送状态")
    private String status;
    
    @Schema(description = "发送时间")
    private LocalDateTime sentTime;
    
    @Schema(description = "送达时间")
    private LocalDateTime deliveredTime;
    
    @Schema(description = "打开时间")
    private LocalDateTime openedTime;
    
    @Schema(description = "点击时间")
    private LocalDateTime clickedTime;
    @Schema(description = "附件列表")
    private List<EmailAttachmentVo> attachments;
    
    @Schema(description = "邮件服务提供商")
    private String provider;
    
    @Data
    @Schema(description = "邮件附件")
    public static class EmailAttachmentVo {
        @Schema(description = "文件名")
        private String fileName;
        
        @Schema(description = "文件URL")
        private String fileUrl;
    }
}
