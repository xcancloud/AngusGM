package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "备份统计数据")
public class BackupStatsVo {
    @Schema(description = "备份总数")
    private Long totalCount;
    
    @Schema(description = "已完成备份数")
    private Long completedCount;
    
    @Schema(description = "失败备份数")
    private Long failedCount;
    
    @Schema(description = "总大小")
    private Long totalSize;
    
    @Schema(description = "最后备份时间")
    private LocalDateTime lastBackupTime;
}
