package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

@Data
public class BackupStatsVo {
    private Long totalCount;
    private Long completedCount;
    private Long failedCount;
    private Long totalSize;
}
