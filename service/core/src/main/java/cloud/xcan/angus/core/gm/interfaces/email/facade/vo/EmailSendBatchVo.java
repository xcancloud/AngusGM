package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.util.List;

@Data
public class EmailSendBatchVo {
    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private List<EmailSendResultVo> results;
    
    @Data
    public static class EmailSendResultVo {
        private String to;
        private String status;
        private String messageId;
    }
}
