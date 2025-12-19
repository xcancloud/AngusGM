package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BackupListVo {
    private Long id;
    private String name;
    private BackupType type;
    private BackupStatus status;
    private Long fileSize;
    private LocalDateTime createdAt;
}
