package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "配额告警VO")
public class QuotaAlertVo implements Serializable {
    
    @Schema(description = "告警ID")
    private String id;
    
    @Schema(description = "租户ID")
    private String tenantId;
    
    @Schema(description = "租户名称")
    private String tenantName;
    
    @Schema(description = "资源类型")
    private String resourceType;
    
    @Schema(description = "资源名称")
    private String resourceName;
    
    @Schema(description = "告警级别")
    private String level;
    
    @Schema(description = "配额")
    private String quota;
    
    @Schema(description = "已使用")
    private String used;
    
    @Schema(description = "使用百分比")
    private Double usagePercent;
    
    @Schema(description = "阈值")
    private Integer threshold;
    
    @Schema(description = "告警信息")
    private String message;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "告警时间")
    private LocalDateTime alertTime;
    
    @Schema(description = "是否已通知")
    private Boolean notified;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
}
