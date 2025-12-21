package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "审计日志导出状态VO")
public class AuditLogExportStatusVo implements Serializable {
    
    @Schema(description = "任务ID")
    private String taskId;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "进度（%）")
    private Integer progress;
    
    @Schema(description = "总记录数")
    private Long totalRecords;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "完成时间")
    private LocalDateTime completeTime;
    
    @Schema(description = "文件大小")
    private String fileSize;
    
    @Schema(description = "下载链接")
    private String downloadUrl;
    
    @Schema(description = "过期时间")
    private LocalDateTime expiryTime;
}
