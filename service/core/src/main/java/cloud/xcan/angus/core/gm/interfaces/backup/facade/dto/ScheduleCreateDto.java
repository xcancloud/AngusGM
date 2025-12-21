package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import lombok.Data;

@Data
public class ScheduleCreateDto {
    private String name;
    private BackupType type;
    private String frequency;
    private String time;
    private String retention;
    private String status;
}