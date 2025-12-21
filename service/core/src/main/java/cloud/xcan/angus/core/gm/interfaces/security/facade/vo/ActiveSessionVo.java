package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActiveSessionVo {
    private String id;
    private Long userId;
    private String userName;
    private String ipAddress;
    private String location;
    private String device;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private LocalDateTime expiresAt;
    private Boolean isCurrent;
}
