package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SecurityEventHandleVo {
    private Long id;
    private Boolean handled;
    private Long handledBy;
    private LocalDateTime handledTime;
    private String handledNote;
}
