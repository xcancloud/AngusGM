package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import lombok.Data;

@Data
public class BackupCreateDto {
    private String name;
    private BackupType type;
    private String sourcePath;
    private String backupPath;
    private Integer retentionDays;
    private Boolean autoDelete;
    private String description;
}
