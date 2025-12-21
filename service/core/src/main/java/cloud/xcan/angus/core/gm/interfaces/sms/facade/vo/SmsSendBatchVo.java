package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.util.List;

@Data
public class SmsSendBatchVo {
    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private List<SmsSendResultVo> results;
    
    @Data
    public static class SmsSendResultVo {
        private String phone;
        private String status;
        private String messageId;
    }
}
