package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保留策略VO")
public class RetentionPolicyVo extends TenantAuditingVo {
    
    @Schema(description = "策略ID")
    private String id;
    
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
