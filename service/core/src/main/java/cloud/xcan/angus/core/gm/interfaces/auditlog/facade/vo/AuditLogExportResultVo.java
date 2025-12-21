package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "审计日志导出结果VO")
public class AuditLogExportResultVo implements Serializable {
    
    @Schema(description = "任务ID")
    private String taskId;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "预计耗时")
    private String estimatedTime;
}
