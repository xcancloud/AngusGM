package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "备份统计数据")
public class BackupStatsVo {
    @Schema(description = "备份总数")
    private Long totalBackups;
    
    @Schema(description = "成功备份数")
    private Long successBackups;
    
    @Schema(description = "失败备份数")
    private Long failedBackups;
    
    @Schema(description = "总大小（格式化的字符串，如：125.5 GB）")
    private String totalSize;
    
    @Schema(description = "最后备份时间（格式化的字符串，如：2024-12-03 02:00:00）")
    private String lastBackupTime;
}
