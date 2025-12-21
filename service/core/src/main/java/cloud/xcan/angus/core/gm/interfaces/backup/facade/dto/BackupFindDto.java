package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BackupFindDto extends cloud.xcan.angus.core.gm.domain.PageQuery {
    private String name;
    private BackupType type;
    private BackupStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
