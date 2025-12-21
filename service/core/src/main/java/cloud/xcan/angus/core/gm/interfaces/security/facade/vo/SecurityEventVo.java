package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SecurityEventVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String type;
    private String level;
    private Long userId;
    private String userName;
    private String ipAddress;
    private String location;
    private String description;
    private LocalDateTime eventTime;
    private Boolean handled;
}
