package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmailRecordVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String content;
    private Long templateId;
    private String templateName;
    private String status;
    private LocalDateTime sentTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime openedTime;
    private LocalDateTime clickedTime;
    private List<String> attachments;
    private String provider;
}
