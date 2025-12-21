package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "保留策略更新DTO")
public class RetentionPolicyUpdateDto implements Serializable {
    
    @Schema(description = "info级别日志保留天数")
    private Integer infoRetentionDays;
    
    @Schema(description = "warning级别日志保留天数")
    private Integer warningRetentionDays;
    
    @Schema(description = "error级别日志保留天数")
    private Integer errorRetentionDays;
    
    @Schema(description = "是否启用自动清理")
    private Boolean autoCleanupEnabled;
    
    @Schema(description = "清理调度表达式")
    private String cleanupSchedule;
}
