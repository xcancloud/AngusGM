package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "配额告警规则VO")
public class QuotaAlertRulesVo implements Serializable {
    
    @Schema(description = "规则ID")
    private String id;
    
    @Schema(description = "用户配额告警规则")
    private AlertRule users;
    
    @Schema(description = "存储配额告警规则")
    private AlertRule storage;
    
    @Schema(description = "应用配额告警规则")
    private AlertRule applications;
    
    @Schema(description = "API调用配额告警规则")
    private AlertRule apiCalls;
    
    @Schema(description = "通知渠道")
    private List<String> notifyChannels;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
    
    @Schema(description = "修改时间")
    private LocalDateTime modifiedDate;
    
    @Data
    @Schema(description = "告警规则")
    public static class AlertRule implements Serializable {
        @Schema(description = "警告阈值（%）")
        private Integer warningThreshold;
        
        @Schema(description = "严重阈值（%）")
        private Integer criticalThreshold;
        
        @Schema(description = "是否启用")
        private Boolean enabled;
    }
}
