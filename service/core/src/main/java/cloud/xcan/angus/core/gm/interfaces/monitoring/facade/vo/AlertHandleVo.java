package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertHandleVo {
    private Long id;
    private String status;
    private Long handledBy;
    private LocalDateTime handledTime;
    private String handleNote;
}
