package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

@Data
public class ScheduleStatusVo {
    private Long id;
    private String status;
    private java.time.LocalDateTime modifiedDate;
}