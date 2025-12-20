package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import lombok.Data;

@Data
public class BackupUpdateDto {
    private Long id;
    private String name;
    private BackupStatus status;
    private Integer retentionDays;
    private Boolean autoDelete;
    private String description;
}
