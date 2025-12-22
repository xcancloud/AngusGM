package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "备份恢复响应")
public class BackupRestoreVo {
    @Schema(description = "备份ID")
    private Long backupId;
    
    @Schema(description = "恢复任务ID")
    private Long restoreId;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "开始时间")
    private java.time.LocalDateTime startTime;
}