package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

@Data
public class ScheduleRunVo {
    private String scheduleId;
    private String backupId;
    private java.time.LocalDateTime startTime;
}