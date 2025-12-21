package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionConfigVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private Integer timeout;
    private Integer maxConcurrent;
    private Boolean allowMultipleDevices;
    private Boolean autoLogoutEnabled;
    private Integer rememberMeDays;
}
