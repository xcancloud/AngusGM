package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailTrackingVo {
    private Long emailId;
    private String subject;
    private LocalDateTime sentTime;
    private LocalDateTime deliveredTime;
    private Boolean opened;
    private LocalDateTime openedTime;
    private Integer openCount;
    private Boolean clicked;
    private Integer clickCount;
    private Boolean bounced;
    private Boolean complained;
}
