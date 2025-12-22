package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量发送邮件响应VO")
public class EmailSendBatchVo {
    
    @Schema(description = "总数量")
    private Integer totalCount;
    
    @Schema(description = "成功数量")
    private Integer successCount;
    
    @Schema(description = "失败数量")
    private Integer failedCount;
    
    @Schema(description = "发送结果列表")
    private List<EmailSendResultVo> results;
    
    @Data
    @Schema(description = "发送结果")
    public static class EmailSendResultVo {
        
        @Schema(description = "收件人邮箱")
        private String to;
        
        @Schema(description = "发送状态")
        private String status;
        
        @Schema(description = "消息ID")
        private String messageId;
    }
}
