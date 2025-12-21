package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

@Data
public class BackupRestoreVo {
    private String backupId;
    private String restoreId;
    private String status;
    private java.time.LocalDateTime startTime;
}