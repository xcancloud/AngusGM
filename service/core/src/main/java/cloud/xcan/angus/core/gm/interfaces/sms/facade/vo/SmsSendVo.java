package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsSendVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String phone;
    private String content;
    private Long templateId;
    private String status;
    private LocalDateTime sentTime;
    private String messageId;
}
