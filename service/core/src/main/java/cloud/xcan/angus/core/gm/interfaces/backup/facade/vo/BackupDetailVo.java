package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BackupDetailVo {
    private Long id;
    private String name;
    private BackupType type;
    private BackupStatus status;
    private String sourcePath;
    private String backupPath;
    private Long fileSize;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer retentionDays;
    private Boolean autoDelete;
    private Boolean verified;
    private String description;
    private LocalDateTime createdAt;
}
