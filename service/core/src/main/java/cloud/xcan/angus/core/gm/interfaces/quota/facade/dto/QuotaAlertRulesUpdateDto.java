package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "配额告警规则更新DTO")
public class QuotaAlertRulesUpdateDto implements Serializable {
    
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
