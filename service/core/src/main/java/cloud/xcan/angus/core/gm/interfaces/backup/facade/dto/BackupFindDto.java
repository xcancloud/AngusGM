package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import lombok.Data;

@Data
public class BackupFindDto {
    private String name;
    private BackupType type;
    private BackupStatus status;
}
