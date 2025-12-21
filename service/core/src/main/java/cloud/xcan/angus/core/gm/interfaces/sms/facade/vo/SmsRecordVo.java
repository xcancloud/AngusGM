package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SmsRecordVo {
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
    private String templateName;
    private String status;
    private LocalDateTime sentTime;
    private LocalDateTime deliveredTime;
    private String provider;
    private BigDecimal cost;
}
