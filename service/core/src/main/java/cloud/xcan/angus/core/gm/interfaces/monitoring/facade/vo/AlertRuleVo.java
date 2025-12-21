package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlertRuleVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String name;
    private String metric;
    private String condition;
    private Double threshold;
    private Integer duration;
    private String level;
    private String status;
    private List<String> notifyChannels;
    private Long triggerCount;
}
