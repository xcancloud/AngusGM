package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "备份计划详情")
public class ScheduleDetailVo extends TenantAuditingVo {
    @Schema(description = "计划ID")
    private Long id;
    
    @Schema(description = "计划名称")
    private String name;
    
    @Schema(description = "备份类型")
    private cloud.xcan.angus.core.gm.domain.backup.BackupType type;
    
    @Schema(description = "频率")
    private String frequency;
    
    @Schema(description = "时间")
    private String time;
    
    @Schema(description = "保留策略")
    private String retention;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "上次运行时间")
    private java.time.LocalDateTime lastRun;
    
    @Schema(description = "下次运行时间")
    private java.time.LocalDateTime nextRun;
    
    @Schema(description = "创建时间")
    private java.time.LocalDateTime createdAt;
}