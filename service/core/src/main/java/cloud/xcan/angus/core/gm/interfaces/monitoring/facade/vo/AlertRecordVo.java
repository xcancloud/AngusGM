package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertRecordVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private Long ruleId;
    private String ruleName;
    private String level;
    private String metric;
    private Double currentValue;
    private Double threshold;
    private String message;
    private String status;
    private LocalDateTime triggerTime;
    private Boolean notified;
}
